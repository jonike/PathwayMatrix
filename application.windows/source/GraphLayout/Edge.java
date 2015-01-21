package GraphLayout;

import java.awt.Color;

import main.Pathway2;
import main.PathwayView;
import main.Slider2;
import processing.core.PApplet;

//Copyright 2005 Sean McCullough
//banksean at yahoo

public class Edge {
	float k=0.05f; //stiffness
	public float naturalLength=1; //natural length.  ehmm uh, huh huh stiffness. natural length ;-)
	Node to;
	Node from;
	int type = -1;  
	Graph g;
	PApplet parent;

	public Edge(Node from_, Node to_,int type_, PApplet papa) {
		parent = papa;
		from = from_;
		to = to_;
		type = type_;  	// type=0: causality
						// type=1: same input
						// type=2: same output
	   naturalLength = Slider2.val;
	}
	
	public float getNaturalLength() {
	    return naturalLength;
	 }
	  
	public void setGraph(Graph h) {
		g = h;
	}

	public Node getTo() {
		return to;
	}

	public Node getFrom() {
		return from;
	}

	public void setTo(Node n) {
		to = n;
	}

	public void setFrom(Node n) {
		from = n;
	}

	public float dX() {
		return to.getX() - from.getX();
	}

	public float dY() {
		return to.getY() - from.getY();
	}

	public Vector3D getForceTo() {
	    float dx = dX();
	    float dy = dY();
	    float l = PApplet.sqrt(dx*dx + dy*dy);
	    float f = k*(l-naturalLength);
	    return new Vector3D(-f*dx/l, -f*dy/l, 0);
	  }
	    
	  public Vector3D getForceFrom() {
	    float dx = dX();
	    float dy = dY();
	    float l = PApplet.sqrt(dx*dx + dy*dy);
	    float f = k*(l-naturalLength);
	    
	    return new Vector3D(f*dx/l, f*dy/l, 0);
	  }

	  public void draw() {
	    if (parent!=null && g!=null){
	    	parent.strokeWeight(1);
	        if (g.getHoverNode() ==null){
    	    	drawLink(240);
    	    }
	    	else if (g.getHoverNode().equals(from) ||
	    		g.getHoverNode().equals(to)){ 
	    		parent.strokeWeight(2);
		        drawLink(255);
		     	from.isConnected =true;
			    to.isConnected = true;
	    	}
	    	else{
	    		drawLink(15);
	     	}
	 	}
	  }
	  
	 
	 public void drawLink(float sat) {
		Pathway2 pathwayFrom = from.parentPathway;
		Pathway2 pathwayTo = to.parentPathway;
		if (pathwayFrom==null || pathwayTo==null) return;
		
			
		if (PathwayView.isExpandedAll){
				
			 if (pathwayFrom.equals(pathwayTo)){
				 drawArc(sat);
			 }
			 else{
				 if (sat<200) return;
				 //System.out.println(pathwayFrom+"	"+pathwayTo);
				 float xFrom = from.iX.value;
				 float yFrom = from.iY.value;
				 float xTo = to.iX.value;
				 float yTo = to.iY.value;
				
				 float xPathwayFrom = pathwayFrom.xPathway;
				 float yPathwayFrom = pathwayFrom.yPathway;
				 float xPathwayTo = pathwayTo.xPathway;
				 float yPathwayTo = pathwayTo.yPathway;
					
				 drawGradientLine(xFrom, yFrom, xPathwayFrom, yPathwayFrom, Color.CYAN);
				 drawGradientLine(xPathwayTo, yPathwayTo, xTo, yTo, Color.BLUE);
				 
				// while()
				// System.out.println(pathwayFrom+"	1 pathwayTo="+pathwayTo);
				  drawPathwayLink(pathwayFrom, pathwayTo);
		 }
		}
		else{
			 drawArc(sat);
		}
	 }
	 public void drawPathwayLink(Pathway2 pathwayFrom, Pathway2 pathwayTo) {
		 float xPathwayFrom = pathwayFrom.xPathway;
		 float yPathwayFrom = pathwayFrom.yPathway;
		 //System.out.println(pathwayFrom+"	2 pathwayTo="+pathwayTo);
		 float xPathwayTo = pathwayTo.xPathway;
		 float yPathwayTo = pathwayTo.yPathway;
		 Pathway2 newPathwayFrom = pathwayFrom;
		 if (pathwayTo.level>0){
			 while(newPathwayFrom.level>pathwayTo.level){
				 drawGradientLine(newPathwayFrom.xPathway, newPathwayFrom.yPathway, 
						 newPathwayFrom.parentPathway.xPathway, newPathwayFrom.parentPathway.yPathway,Color.MAGENTA);
				 newPathwayFrom = newPathwayFrom.parentPathway;
			 }
		 }
		 Pathway2 newPathwayTo = pathwayTo;
		 if (pathwayFrom.level>0){
			 while(newPathwayTo.level>pathwayFrom.level){
				 drawGradientLine(newPathwayTo.parentPathway.xPathway, newPathwayTo.parentPathway.yPathway, 
						 newPathwayTo.xPathway, newPathwayTo.yPathway,Color.RED);
				 newPathwayTo = newPathwayTo.parentPathway;
			 }
		 }
		 while (!newPathwayFrom.parentPathway.equals(newPathwayTo.parentPathway)){
			 drawGradientLine(newPathwayFrom.xPathway, newPathwayFrom.yPathway, 
					 newPathwayFrom.parentPathway.xPathway, newPathwayFrom.parentPathway.yPathway,new Color(120,0,120));
			drawGradientLine(newPathwayTo.parentPathway.xPathway, newPathwayTo.parentPathway.yPathway, 
					 newPathwayTo.xPathway, newPathwayTo.yPathway,new Color(100,0,0));
			 newPathwayFrom = newPathwayFrom.parentPathway;
			newPathwayTo = newPathwayTo.parentPathway;
		 }
		 drawGradientLine(newPathwayFrom.xPathway, newPathwayFrom.yPathway, 
				 newPathwayTo.xPathway, newPathwayTo.yPathway,new Color(0,0,0));
		
		 
	 }
			
			
	 public void drawGradientLine(float x1, float y1, float x2, float y2, Color color) {
		 int numSec =6;
		 for (int i=1;i<=numSec;i++){
				float sss = (float) i/numSec;
				float x3 = x1+(x2-x1)*sss;
				float y3 = y1+(y2-y1)*sss;
				float r = 200*sss;
				
				parent.stroke(color.getRGB());
				parent.line(x1,y1,x3,y3);
				x1=x3;
				y1=y3;
		 }
	 }
			
			
	  public void drawArc(float sat) {
		// Draw gradient lines
		/*int numSec =6;
		float x1 = from.iX.value;
		float y1 = from.iY.value;
		for (int i=1;i<=numSec;i++){
			float sss = (float) i/numSec;
			float x2 = from.iX.value+(to.iX.value-from.iX.value)*sss;
			float y2 = from.iY.value+(to.iY.value-from.iY.value)*sss;
			float sat2 = 255*sss;
			float r = 255-sat2;
			
			parent.stroke(r,r,0,sat2);
			parent.line(x1,y1,x2,y2);
			x1=x2;
			y1=y2;
		}*/
		
 	    	float alFrom = from.iAlpha.target;
			//float x1 = from.iX.value-from.difX;
			//float y1 = from.iY.value-from.difY;
			float x1 = from.iX.value;
			float y1 = from.iY.value;
			
			float alTo = to.iAlpha.target;
			//float x2 = to.iX.value-to.difX;
			//float y2 = to.iY.value-to.difY;
			float x2 = to.iX.value;
			float y2 = to.iY.value;
			
			
			float alpha = (y2-y1)/(x2-x1);
			alpha = PApplet.atan(alpha);
			float dis = (y2-y1)*(y2-y1)+(x2-x1)*(x2-x1);
			float dd = PApplet.sqrt(dis);
			
			float alCircular = PApplet.PI -PApplet.abs(alTo-alFrom);
			 if (PathwayView.popupLayout.s==1 || PathwayView.popupLayout.s==0)
				 alCircular += PathwayView.iTransition.value;
			 else if (PathwayView.popupLayout.s==2)
				 alCircular *= PathwayView.iTransition.value;
			 else if (PathwayView.popupLayout.s==3)
				 alCircular *= PathwayView.iTransition.value;
			
			 if (alCircular<0.01f)
				 alCircular=0.01f;
			 else if (alCircular>PApplet.PI-0.01f)
				 alCircular = PApplet.PI-0.01f;
			 float newR = (dd/2)/PApplet.sin(alCircular/2);
	    	 float d3 = PApplet.dist(x1,y1,x2,y2);
	    	 float x11 = (x1+x2)/2 - ((y1-y2)/2)*PApplet.sqrt(PApplet.pow(newR*2/d3,2)-1);
	    	 float y11 = (y1+y2)/2 + ((x1-x2)/2)*PApplet.sqrt(PApplet.pow(newR*2/d3,2)-1);
	    	 float x22 = (x1+x2)/2 + ((y1-y2)/2)*PApplet.sqrt(PApplet.pow(newR*2/d3,2)-1);
	    	 float y22 = (y1+y2)/2 - ((x1-x2)/2)*PApplet.sqrt(PApplet.pow(newR*2/d3,2)-1);
		
	    	 float x3 =0, y3=0;
	    	 float d11 = PApplet.dist(x11, y11, PathwayView.xCircular, PathwayView.yCircular);
	    	 float d22 = PApplet.dist(x22, y22, PathwayView.xCircular, PathwayView.yCircular);
	    	 if (d11>d22){
	    		 x3=x11;
	    		 y3=y11;
	    	 }
	    	 else if (d11<d22){
	    		 x3=x22;
	    		 y3=y22;
	    	 }
		   
			float delX1 = (x1-x3);
			float delY1 = (y1-y3);
			float delX2 = (x2-x3);
			float delY2 = (y2-y3);
			float al1 = PApplet.atan2(delY1,delX1);
			float al2 = PApplet.atan2(delY2,delX2);
			if (al1-al2>PApplet.PI)
				al1=al1-2*PApplet.PI;
			else if (al2-al1>PApplet.PI)
				al2=al2-2*PApplet.PI;
			parent.noFill();
			
			if (type==0){
				if (al1<al2)
					drawArc(x3, y3, newR*2,  al1, al2, sat);
				else
					drawArc(x3, y3, newR*2,  al2, al1, sat);
			}
			else if (type==1){
				parent.stroke(255,0,255);
				if (al1<al2)
					parent.arc(x3, y3, newR*2, newR*2,  al1, al2);
				else
					parent.arc(x3, y3, newR*2, newR*2,  al2, al1);
			}
			else if (type==2){
				parent.stroke(0,255,0);
				if (al1<al2)
					parent.arc(x3, y3, newR*2, newR*2,  al1, al2);
				else
					parent.arc(x3, y3, newR*2, newR*2,  al2, al1);
			}
	  }

	  
	  public void drawArc(float x3, float y3, float d3, float al1, float al2, float sat){
			float x1 = x3+d3/2*PApplet.cos(al1);
			float y1 = y3+d3/2*PApplet.sin(al1);
			boolean down = true;
			if (PApplet.dist(x1, y1, from.iX.value-from.difX, from.iY.value-from.difY)
					>PApplet.dist(x1, y1, to.iX.value-to.difX, to.iY.value-to.difY))
				down = false;
			
			if (PathwayView.popupLayout.s==0 && PApplet.dist(from.iX.value,from.iY.value,from.iX.target,from.iY.target)<2){
				float x11 = from.iX.value-from.difX;
				float y11 = from.iY.value-from.difY;
				float x22 = to.iX.value-to.difX;
				float y22 = to.iY.value-to.difY;
				x3 = (x11+x22)/2;
				y3 = (y11+y22)/2;
				al1 = -PApplet.PI/2;
				al2 = PApplet.PI/2;
				if (from.iY.value<to.iY.value)
					down = true;
				else
					down = false;
			}
			if (PathwayView.popupLayout.s==1 && PApplet.dist(from.iX.value,from.iY.value,from.iX.target,from.iY.target)<2){
				float x11 = from.iX.value-from.difX;
				float y11 = from.iY.value-from.difY;
				float x22 = to.iX.value-to.difX;
				float y22 = to.iY.value-to.difY;
				x3 = (x11+x22)/2;
				y3 = (y11+y22)/2;
				
				if (from.iY.value<to.iY.value){
					al1 = PApplet.PI/2;
					al2 = PApplet.PI*3/2;
				}
				else{
					al1 = -PApplet.PI/2;
					al2 = PApplet.PI/2;
				}
				down = false;
			}
			
			int numSec = 15;
			float beginAngle = al1;
			if (al2<al1)
				beginAngle = al2;
			for (int k=1;k<=numSec;k++){
				float endAngle = al1+k*(al2-al1)/numSec;
				parent.noFill();
				float sss = (float) k/numSec;
				if (!down)
					sss = (float) (numSec-k)/numSec;
				float sat2 = sat*sss;
				float r = sat-sat2;
				
				float minSat = PApplet.min(50, sat);
				if(sat2<minSat)
					sat2=minSat;
				
				parent.stroke(r,r,0,sat2);
				parent.arc(x3, y3, d3,d3, beginAngle, endAngle);
				beginAngle = endAngle;
			}
			
			
	}	  
}
