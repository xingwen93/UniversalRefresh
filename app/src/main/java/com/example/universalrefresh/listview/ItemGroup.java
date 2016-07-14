package com.example.universalrefresh.listview;

public class ItemGroup implements Item {
	private String groupName;

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	@Override
	public int getItemViewType() {
		return TYPE_GROUP;
	}

}
