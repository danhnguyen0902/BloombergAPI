package com.bemu.BEmu.IntradayBarRequest;

import com.bemu.BEmu.Element;
import com.bemu.BEmu.Name;
import com.bemu.BEmu.types.RandomDataGenerator;

public class ElementIntradayBarResponseError extends Element
{
    private final ElementIntradayBarString _source, _category, _message, _subCategory;
    private final ElementIntradayBarInt _code;

    ElementIntradayBarResponseError(String security)
    {
        int code = RandomDataGenerator.randomInt(99);
        String sourceGibberish = RandomDataGenerator.randomString(5).toLowerCase();

        this._source = new ElementIntradayBarString("source", String.format("%s::%s%s", code, sourceGibberish, RandomDataGenerator.randomInt(99)));
        this._code = new ElementIntradayBarInt("code", code);
        this._category = new ElementIntradayBarString("category", "BAD_SEC");
        this._message = new ElementIntradayBarString("message", String.format("Unknown/Invalid security [nid:%s]", code));
        this._subCategory = new ElementIntradayBarString("subcategory", "INVALID_SECURITY");
    }
    
    public Name name()
    {
    	return new Name("responseError");
    }
    
    public int numValues()
    {
    	return 1;
    }
    
    public int numElements()
    {
    	return 5;
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
    	if(name.equals("source"))
    		return this._source;
    	
    	else if(name.equals("category"))
    		return this._category;
    	
    	else if(name.equals("message"))
    		return this._message;
    	
    	else if(name.equals("subcategory"))
    		return this._subCategory;
    	
    	else if(name.equals("code"))
    		return this._code;
    	
    	else
    		return super.getElement(name);
    }
    
    public boolean hasElement(String name)
    {
    	return name.equals("source") ||
    			name.equals("category") ||
    			name.equals("message") ||
    			name.equals("subcategory") ||
    			name.equals("code");
    }
    
    protected StringBuilder prettyPrint(int tabIndent)
    {
        String tabs = com.bemu.BEmu.types.IndentType.Indent(tabIndent);
        StringBuilder result = new StringBuilder();
        
        result.append(String.format("%s%s = {%s", tabs, this.name(), System.getProperty("line.separator")));

        result.append(this._source.prettyPrint(tabIndent + 1));
        result.append(this._code.prettyPrint(tabIndent + 1));
        result.append(this._category.prettyPrint(tabIndent + 1));
        result.append(this._message.prettyPrint(tabIndent + 1));
        result.append(this._subCategory.prettyPrint(tabIndent + 1));

        result.append(String.format("%s}%s", tabs, System.getProperty("line.separator")));

        return result;
    }
    

}
