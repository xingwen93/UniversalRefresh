package com.example.universalrefresh.listview;

public class ItemGroup implements Item {
	private String groupName;
	private int groupPosition;

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

	@Override
	public int getGroupPosition() {
		return groupPosition;
	}

	@Override
	public void setGroupPosition(int groupPosition) {
		this.groupPosition = groupPosition;
	}

}
