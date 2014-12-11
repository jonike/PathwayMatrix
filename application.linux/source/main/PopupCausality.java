package main;
import java.awt.Color;
import java.text.DecimalFormat;

import processing.core.PApplet;
import processing.core.PFont;

public class PopupCausality{
	public int b = -1;
	public static int s=-1;
	public PApplet parent;
	public float x = 800;
	public int y = 0;
	public int w1 = 100;
	public int w = 300;
	public int h;
	public int itemH = 20;
	public Color cGray  = new Color(240,240,240);
	public static String[] items={"All Causalities","Knockout a protein", "Shortest path", "Feedback loop"}; 
	public Slider slider;

	public PopupCausality(PApplet parent_){
		parent = parent_;
		slider =  new Slider(parent_,5);
	}
	
	public void draw(float x_){
		x = x_;
		if (main.PathwayViewer_1_7.popupRelation.b<0)
			checkBrushing();
		if (b>=0){
			parent.fill(100);
			parent.stroke(0);
			parent.textSize(13);
			h=items.length*itemH+40;
			parent.fill(200);
			parent.stroke(0,150);
			parent.rect(x, y+25, w,h);
			
			// Max number of relations
			float max =-1;
			for (int j=0;j<main.PathwayViewer_1_7.pairs.length;j++){
				float sqrt = PApplet.sqrt(main.PathwayViewer_1_7.pairs[j].size());
				if (sqrt>max)
					max = sqrt;
			}
			for (int i=0;i<items.length;i++){
				if (i==s){
					parent.noStroke();
					parent.fill(0);
					parent.rect(x+10,y+itemH*(i)+5+25,w-25,itemH+1);
					parent.fill(255,255,0);
				}
				else if (i==b){
					parent.fill(200,0,0);
				}
				else{
					parent.fill(0);
				}
				parent.textAlign(PApplet.LEFT);
				parent.text(items[i],x+30,y+itemH*(i+1)+25);  // 
			}	
		}
		
		if (s>=0 && s<items.length){
			for (int i=0;i<w1;i++){
				parent.stroke(255,255-i*2.55f,i*2.55f);
				parent.line(x+i, y, x+i, y+24);
			}
		}
		else{
			parent.fill(150);
			parent.noStroke();
			parent.rect(x,y,w1,25);
		}
		parent.fill(0);
		parent.textAlign(PApplet.CENTER);
		parent.textSize(13);
		parent.text("Causality",x+w1/2,y+18);
		
	}
	
	 public void mouseClicked() {
		if(s!=b){
			s = b;
		}
		else{
			s = -100;
		}
		
	}
	 
	public void checkBrushing() {
		int mX = parent.mouseX;
		int mY = parent.mouseY;
		if (x<mX && mX<x+w1 && y<=mY && mY<=itemH+5){
			b =100;
			return;
		}
		else{
			for (int i=0; i<items.length; i++){
				if (x<=mX && mX<=x+w && y+itemH*i+25<=mY && mY<=y+itemH*(i+1)+6+25){
					b =i;
					return;
				}	
			}
			if (x<=mX && mX<=x+w && y<=mY && mY<=y+h){
				return;
			}	
		}
		b =-1;
	}
	
	
	
}