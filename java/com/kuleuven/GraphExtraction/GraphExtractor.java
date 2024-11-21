package com.kuleuven.GraphExtraction;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JarTypeSolver;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GraphExtractor {

    private static JavaParser javaParser;
    private static final JSONObject graph = new JSONObject();
    private static final JSONArray nodes = new JSONArray();
    private static final JSONArray edges = new JSONArray();
    private static final Set<String> declaredClasses = new HashSet<>();
    private static final Set<String> uniqueEdges = new HashSet<>();

    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            System.err.println("Usage: java CollaborationDiagramExtractor <output_json_file_path> <source_directory> <jar_path>]");
            return;
        }

        String outputFilePath = args[0];
        File mainDirectory = new File(args[1]);
        Path jarPath = Paths.get(args[2]);

        setupParser(jarPath, mainDirectory);

        if (mainDirectory.isDirectory()) {
            Files.walk(mainDirectory.toPath())
                .filter(Files::isRegularFile)
                .filter(path -> path.toString().endsWith(".java"))
                .forEach(path -> collectClassNames(path.toFile()));
        }

        if (mainDirectory.isDirectory()) {
            Files.walk(mainDirectory.toPath())
                .filter(Files::isRegularFile)
                .filter(path -> path.toString().endsWith(".java"))
                .forEach(path -> parseJavaFile(path.toFile()));
        }

        processEdges();

        graph.put("nodes", nodes);
        graph.put("edges", edges);

        GraphUtils.writeFile(outputFilePath, graph.toString(4).getBytes());
        System.out.println("Graph has been saved to " + outputFilePath);
    }

    private static void setupParser(Path jarPath, File mainDirectory) {
        CombinedTypeSolver combinedSolver = new CombinedTypeSolver(new ReflectionTypeSolver());
        
        if (mainDirectory.exists() && mainDirectory.isDirectory()) {
            combinedSolver.add(new JavaParserTypeSolver(mainDirectory));
        } else {
            System.err.println("Directory does not exist: " + mainDirectory.getPath());
        }

        try {
            combinedSolver.add(new JarTypeSolver(jarPath));
            combinedSolver.add(new JarTypeSolver("target/libs/javax.servlet-api-4.0.1.jar"));
            combinedSolver.add(new JarTypeSolver("target/libs/xercesImpl-2.8.0.jar"));
            combinedSolver.add(new JarTypeSolver("target/libs/xml-apis-1.3.03.jar"));
        } catch (IOException e) {
            System.err.println("Failed to load JAR for type resolution: " + e.getMessage());
        }        

        JavaSymbolSolver symbolSolver = new JavaSymbolSolver(combinedSolver);
        ParserConfiguration parserConfiguration = new ParserConfiguration().setSymbolResolver(symbolSolver);
        javaParser = new JavaParser(parserConfiguration);
    }

    private static void collectClassNames(File file) {
        try (FileInputStream in = new FileInputStream(file)) {
            ParseResult<CompilationUnit> parseResult = javaParser.parse(in);
            parseResult.ifSuccessful(cu -> {
                cu.findAll(ClassOrInterfaceDeclaration.class).forEach(cid -> {
                    cid.getFullyQualifiedName().ifPresent(declaredClasses::add);
                });
            });
        } catch (Exception e) {
            System.err.println("Error collecting class names from file: " + file.getName());
        }
    }

    private static void parseJavaFile(File file) {
        try (FileInputStream in = new FileInputStream(file)) {
            
            ParseResult<CompilationUnit> parseResult = javaParser.parse(in);
            parseResult.ifSuccessful(cu -> {
                ClassVisitor classVisitor = new ClassVisitor();
                cu.accept(classVisitor, null);
                List<ClassOrInterfaceDeclaration> classDefinitions = classVisitor.getDeclaredClasses();

                classDefinitions.forEach(classDefinition -> {
                    String className = classDefinition.getFullyQualifiedName().orElse("Unknown");
                    JSONObject node = new JSONObject();
                    node.put("name", className);
                    nodes.put(node);
                    declaredClasses.add(className);

                    MethodVisitor methodVisitor = new MethodVisitor();
                    classDefinition.accept(methodVisitor, null);
                    List<MethodDeclaration> methods = methodVisitor.getMethodDeclarations();
                    methods.forEach(sourceMethod -> {

                        JSONObject sourceMethodJSON = GraphUtils.getMethodJSON(sourceMethod, className);
                        
                        MethodCallVisitor methodCallVisitor = new MethodCallVisitor();
                        sourceMethod.accept(methodCallVisitor, null);
                        List<MethodCallExpr> methodCalls = methodCallVisitor.getMethodCalls();
                        methodCalls.forEach(methodCall -> {
                            String declaringClassName = methodCall.resolve().declaringType().getQualifiedName();
                            String uniqueId = GraphUtils.getUniqueId(methodCall, sourceMethod, className);

                            if (!uniqueEdges.contains(uniqueId)) {
                                JSONObject linkMethodJSON = GraphUtils.getMethodCallJSON(methodCall, declaringClassName);


                                JSONObject edge = new JSONObject();
                                edge.put("source", className);
                                edge.put("destination", declaringClassName);
                                edge.put("link_method", linkMethodJSON);
                                edge.put("source_method", sourceMethodJSON);
                                uniqueEdges.add(uniqueId);
                                edges.put(edge);
                            }
                        });
                    });
                });
            });
        } catch (Exception e) {
            System.err.println("Error processing file: " + file.getName());
        }
    }

    private static void processEdges() {
        JSONArray filteredEdges = new JSONArray();
        edges.forEach(item -> {
            JSONObject edge = (JSONObject) item;
            String source = edge.getString("source");
            String destination = edge.getString("destination");
            if (!source.equals(destination) &&
                declaredClasses.contains(source) && declaredClasses.contains(destination)) {
                filteredEdges.put(edge);
            }
        });
        edges.clear();
        filteredEdges.forEach(edges::put);
    }
}