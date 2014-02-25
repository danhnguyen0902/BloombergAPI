package com.bemu.BEmu;

public class Schema
{
	public enum Datatype
	{
		BOOL(0),
		CHAR(1),
		DATE(2),
		DATETIME(3),
		FLOAT32(5),
		FLOAT64(6),
		INT32(7),
		INT64(8),
		STRING(9),
		TIME(10),
		BYTEARRAY(256),
		ENUMERATION(257),
		SEQUENCE(258),
		CHOICE(259);
		
	    private final int index;   

	    Datatype(int index) {
	        this.index = index;
	    }

	    public int index() { 
	        return index; 
	    }
	}
}
