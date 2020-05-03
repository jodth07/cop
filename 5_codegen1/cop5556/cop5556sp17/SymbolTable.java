package cop5556sp17;

import cop5556sp17.AST.Dec;
import cop5556sp17.TypeCheckVisitor.TypeCheckException;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class SymbolTable {
	private Map<Integer, Map<String, Dec>> scopes = new HashMap<>();
	private int currentScope = 0;

	/**
	 * to be called when block entered
	 */
	public void enterScope(){
		currentScope++;
		scopes.put(currentScope, new HashMap<>());
	}

	/**
	 * leaves scope
	 */
	public void leaveScope(){
		currentScope--;
	}

	public boolean insert(String ident, Dec dec) throws TypeCheckException {
		if (scopes.get(currentScope).containsKey(ident)) {
			throw new TypeCheckException("Cannot declare variable " + ident + " a second time.");
		}
		scopes.get(currentScope).put(ident, dec);
		return true;
	}

	public Dec lookup(String ident){
		Dec dec;
		int scope = currentScope;
		do {
			dec = scopes.get(scope--).get(ident);
		} while(dec == null && scope >= 0);
		return dec;

	}

	public SymbolTable() {
		scopes.put(currentScope, new HashMap<>());
	}

	public int getCurrentScope() {
		return currentScope;
	}

	@Override
	public String toString() {
		//TODO:  IMPLEMENT THIS

		return "";
	}
}
