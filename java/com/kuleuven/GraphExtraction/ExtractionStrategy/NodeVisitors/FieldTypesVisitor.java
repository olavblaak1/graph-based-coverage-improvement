package com.kuleuven.GraphExtraction.ExtractionStrategy.NodeVisitors;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.resolution.types.ResolvedReferenceType;
import com.github.javaparser.resolution.types.ResolvedType;


/**
 * Recursively visits a type, finding all types it references 
 */
public class FieldTypesVisitor extends VoidVisitorAdapter<Void> {
    private List<ResolvedReferenceType> refersToTypes = new LinkedList<>(); 

    @Override
    public void visit(FieldDeclaration fieldDeclaration, Void arg) {
        super.visit(fieldDeclaration, arg);

        fieldDeclaration.getVariables().forEach(variableDeclarator -> {
            ResolvedType type = variableDeclarator.resolve().getType();
            if (type.isReferenceType()) {
                ResolvedReferenceType resolvedReferenceType = type.asReferenceType();
                this.visit(resolvedReferenceType, null);
            }
        });
    }
    private void visit(ResolvedReferenceType referenceType, Void arg) {
        if (referenceType.typeParametersValues().isEmpty()) {
            refersToTypes.add(referenceType);
        }
        else {
            referenceType.typeParametersValues().forEach(typeParameter -> {
                if(typeParameter.isReferenceType()) {
                    this.visit(typeParameter.asReferenceType(), null);
                }
            });
        }
    }

    public List<ResolvedType> getReferredTypes() {
        return new ArrayList<>(refersToTypes);
    }
}
