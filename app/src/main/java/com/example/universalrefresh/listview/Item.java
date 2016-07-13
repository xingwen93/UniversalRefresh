package com.example.universalrefresh.listview;

public interface Item {
	
	public static final int TYPE_GROUP = 0;
	public static final int TYPE_MEMBER = 1;
	
	int getItemViewType();
	
	void setGroupPosition(int groupPosition);
	
	int getGroupPosition();
	
}
