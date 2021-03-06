package fr.inria.astor.approaches.tos.operator.metaevaltos;

import java.util.ArrayList;
import java.util.List;

import fr.inria.astor.core.entities.Ingredient;
import fr.inria.astor.core.entities.IngredientFromDyna;
import fr.inria.astor.core.entities.ModificationPoint;
import fr.inria.astor.core.manipulation.MutationSupporter;
import fr.inria.astor.core.manipulation.sourcecode.VariableResolver;
import fr.inria.lille.repair.expression.access.VariableImpl;
import spoon.reflect.code.CtVariableAccess;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtVariable;
import spoon.reflect.reference.CtTypeReference;

/**
 * 
 * @author Matias Martinez
 *
 */
public class SupportOperators {

	/**
	 * Add all variables from the expression and candidates in a list
	 * 
	 * @param exptochange
	 * @param candidates
	 * @param modificationPoint
	 * @return
	 */
	public static List<CtVariableAccess> collectAllVarsFromDynaIngredients(List<IngredientFromDyna> candidates,
			ModificationPoint modificationPoint) {
		// First collect variables from dynamoth
		List<VariableImpl> dynaVars = new ArrayList<>();
		List<CtVariableAccess> varAccessList = new ArrayList();

		for (IngredientFromDyna candidateIngr : candidates) {
			dynaVars.addAll(candidateIngr.getVariable());
		}

		if (dynaVars.isEmpty()) {
			return varAccessList;
		}
		// Second, for each var from dynamoth, find the CtVariable access (by parsing
		// the context of the modification point)
		List<CtVariable> varAccessCandidate = modificationPoint.getContextOfModificationPoint();

		for (VariableImpl aDynaVariable : dynaVars) {

			for (CtVariable aVariableSinScope : varAccessCandidate) {

				if (aVariableSinScope.getSimpleName().equals(aDynaVariable.getVariableName())) {

					CtVariableAccess aVariableRead = MutationSupporter.getFactory()
							.createVariableRead(aVariableSinScope.getReference(), false);
					if (!varAccessList.contains(aVariableRead))
						varAccessList.add(aVariableRead);
				}
			}

		}

		return varAccessList;
	}

	/**
	 * Retrieves all variables from the target element and all ingredients
	 * 
	 * @param elementtochange
	 * @param candidates
	 * @return
	 */
	public static List<CtVariableAccess> collectAllVars(CtElement elementtochange, List<Ingredient> candidates) {
		List<CtVariableAccess> varAccess = VariableResolver.collectVariableAccess(elementtochange);

		for (Ingredient candidateIngr : candidates) {
			CtElement candidate = candidateIngr.getCode();
			List<CtVariableAccess> varAccessCandidate = VariableResolver.collectVariableAccess(candidate);
			for (CtVariableAccess varX : varAccessCandidate) {
				if (!varAccess.contains(varX)) {
					varAccess.add(varX);
				}
			}
		}

		return varAccess;
	}

	public static boolean compareTypes(CtTypeReference t1, CtTypeReference t2) {
		try {
			return t1 != null && t2 != null && (t1.toString().equals(t2.toString()) || t1.equals(t2)
					|| t1.isSubtypeOf(t2) || t2.isSubtypeOf(t1));
		} catch (Exception e) {
			System.out.println("Error comparing types");
			e.printStackTrace();
			return false;
		}
	}

	public static List getAllMethodsFromClass(CtClass parentClass) {
		List allMethods = new ArrayList(parentClass.getAllMethods());

		if (parentClass != null && parentClass.getParent() instanceof CtClass) {
			CtClass parentParentClass = (CtClass) parentClass.getParent();
			allMethods.addAll(parentParentClass.getAllMethods());

		}
		return allMethods;
	}
}
