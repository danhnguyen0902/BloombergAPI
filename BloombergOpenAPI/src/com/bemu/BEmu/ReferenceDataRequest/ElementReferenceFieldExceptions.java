package com.bemu.BEmu.ReferenceDataRequest;

import com.bemu.BEmu.Element;
import com.bemu.BEmu.Name;

public class ElementReferenceFieldExceptions extends ElementParent
{
    private final ElementReferenceString _fieldId;
    private final ElementReferenceErrorInfo _errorInfo;

    public ElementReferenceFieldExceptions(String badField)
    {
        this._fieldId = new ElementReferenceString("fieldId", badField);
        this._errorInfo = new ElementReferenceErrorInfo();
    }
	
    public Name name()
    {
    	return new Name("fieldExceptions");
    }
    
    public int numValues()
    {
    	return 1;
    }
    
    public int numElements()
    {
    	return 2;
    }
    
    public String getElementAsString(String name) throws Exception
    {
    	return this.getElement(name).getValueAsString();
    }
    
    public int getElementAsInt32(String name) throws Exception
    {
    	return this.getElement(name).getValueAsInt32();
    }
    
    public Element getElement(String name) throws Exception
    {
    	if(name.equals("fieldId"))
    		return this._fieldId;
    	
    	else if(name.equals("errorInfo"))
    		return this._errorInfo;
    	
    	else
    		return super.getElement(name);
    }
    
    public boolean hasElement(String name)
    {
    	return name.equals("fieldId") || name.equals("errorInfo");
    }

    protected StringBuilder prettyPrint(int tabIndent)
    {
        String tabs = com.bemu.BEmu.types.IndentType.Indent(tabIndent);
        StringBuilder result = new StringBuilder();
        
        result.append(String.format("%s%s = {%s", tabs, this.name().toString(), System.getProperty("line.separator")));
        result.append(this._fieldId.prettyPrint(tabIndent + 1));
        result.append(this._errorInfo.prettyPrint(tabIndent + 1));
        result.append(String.format("%s}%s", tabs, System.getProperty("line.separator")));

        return result;
    }
    
    public boolean isComplexType()
    {
    	return true;
    }
    
    public boolean isArray()
    {
    	return false;
    }
    
    public boolean isNull()
    {
    	return false;
    }
}
