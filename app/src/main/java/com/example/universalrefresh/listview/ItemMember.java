package com.example.universalrefresh.listview;

public class ItemMember implements Item {

	private String text;

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

}
