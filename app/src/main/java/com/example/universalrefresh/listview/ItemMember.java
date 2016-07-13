package com.example.universalrefresh.listview;

public class ItemMember implements Item {

	private String text;
	private int groupPosition;
	private int memberPosition;

	public String getText() {
		return text;
	}
	
	public void setText(String text) {
		this.text = text;
	}
	
	@Override
	public int getItemViewType() {
		return TYPE_MEMBER;
	}

	@Override
	public int getGroupPosition() {
		return groupPosition;
	}

	@Override
	public void setGroupPosition(int groupPosition) {
		this.groupPosition = groupPosition;
	}
	
	public int getMemberPosition() {
		return memberPosition;
	}
	
	public void setMemberPosition(int memberPosition) {
		this.memberPosition = memberPosition;
	}

}
