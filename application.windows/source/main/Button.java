package main;
import java.awt.Color;

import processing.core.PApplet;
import processing.core.PFont;

public class Button{
	public int b = -1;
	public PApplet parent;
	public int y = 0;
	public int w = 73;
	public int h = 23;
	public float x2 = 0;
	public int w2 = 200;
	public int itemNum = 9;
	public Color cGray  = new Color(240,240,240);
	public Button(PApplet parent_){
		parent = parent_;
	}
	public int count =0;
	
	
	public void draw(){
		checkBrushing();
		if (b>0){
			parent.fill(0,0,0);
			parent.stroke(155,155,155);
			parent.rect(x2, y, w, h);
			
			parent.textAlign(PApplet.LEFT);
			parent.fill(255);
			parent.text("Browse...",x2+8,y+16);
		}	
		else{
			parent.fill(125,125,125);
			parent.noStroke();
			parent.rect(x2, y, w, h);
			
			parent.textAlign(PApplet.LEFT);
			parent.fill(0);
			parent.text("Browse...",x2+8,y+16);
		}
		
		count++;
	    if (count==10000)
	    	count=200;
	}
	
	 
	public void checkBrushing() {
		int mX = parent.mouseX;
		int mY = parent.mouseY;
		if (x2<mX && mX<x2+w && y<mY && mY<h){
			b =100;
			return;
		}
		b =-1;
	}
	
}