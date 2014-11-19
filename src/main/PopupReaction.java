package main;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.biopax.paxtools.model.level3.Complex;
import org.biopax.paxtools.model.level3.BiochemicalReaction;

import processing.core.PApplet;

public class PopupReaction{
	public static boolean bPopup = false;
	public static boolean sAll = false;
	public static int b = -1000;
	public PApplet parent;
	public float x = 800;
	public static float yBegin = 25;
	public static float yBeginList = 70;
	public int w1 = 100;
	public int w = 600;
	public int h = 28;
	public static int s=-100;
	public static float maxSize = 0;
	public Integrator[] iX, iY, iH;
	public int[] hightlightList;
	public float maxH = 22;
	float hProtein = 0;
	
	public static Map<BiochemicalReaction, Integer> itemHash =  new HashMap<BiochemicalReaction, Integer>();
	
	public String[] proteins = null;
	public Integrator[] iP;
	
	public static CheckBox checkGroup;
	
	public PopupReaction(PApplet parent_){
		parent = parent_;
		checkGroup = new CheckBox(parent, "Lensing");
		
		// Proteins list
	}
	
	public void setItems(){
		int i=0;
		maxSize =0;
		Map<BiochemicalReaction, Integer> unsortMap  =  new HashMap<BiochemicalReaction, Integer>();
		s=-400;
		for (BiochemicalReaction current : main.MainMatrix.reactionSet){
			int size = main.MainMatrix.getAllGenesInComplexById(i).size();
			unsortMap.put(current, size);
			if (size>maxSize)
				maxSize = size;
			i++;
		}
		itemHash = sortByComparator(unsortMap);
		
		// positions of items
		iX = new Integrator[itemHash.size()];
		iY = new Integrator[itemHash.size()];
		iH = new Integrator[itemHash.size()];
		for (i=0;i<itemHash.size();i++){
			iX[i] = new Integrator(x, 0.5f,0.1f);
			iY[i] = new Integrator(20, 0.5f,0.1f);
			iH[i] = new Integrator(10, 0.5f,0.1f);
		}
		
		hightlightList =  new int[itemHash.size()];
		for (i=0;i<itemHash.size();i++){
			hightlightList[i] = -1;
		}
			
		
		proteins =  new String[main.MainMatrix.ggg.size()];
		iP =  new Integrator[main.MainMatrix.ggg.size()];
		for (int p=0; p<main.MainMatrix.ggg.size();p++){
			proteins[p] =  main.MainMatrix.ggg.get(p).name;
			iP[p] =   new Integrator(20, 0.5f,0.1f);
		}
		updateProteinPositions();
	}
	
	public int getProteinIndex(String s){
		for (int p=0; p<proteins.length;p++){
			if (s.contains(proteins[p])){
				return p;
			}
		}
		return -1;
	}
	
	public void updateProteinPositions(){
		hProtein = (parent.height-yBeginList)/(proteins.length);
		if (hProtein>maxH)
			hProtein =maxH;
		for (int p=0; p<proteins.length;p++){
			int order = main.MainMatrix.ggg.get(p).order;
			iP[p].target(yBeginList+hProtein*order);
		}
	}
		
		
	// Sort decreasing order
	public static Map<BiochemicalReaction, Integer> sortByComparator(Map<BiochemicalReaction, Integer> unsortMap) {
		// Convert Map to List
		List<Map.Entry<BiochemicalReaction, Integer>> list = 
			new LinkedList<Map.Entry<BiochemicalReaction, Integer>>(unsortMap.entrySet());
 
		// Sort list with comparator, to compare the Map values
		Collections.sort(list, new Comparator<Map.Entry<BiochemicalReaction, Integer>>() {
			public int compare(Map.Entry<BiochemicalReaction, Integer> o1,
                                           Map.Entry<BiochemicalReaction, Integer> o2) {
				return -(o1.getValue()).compareTo(o2.getValue());
			}
		});
 
		// Convert sorted map back to a Map
		Map<BiochemicalReaction, Integer> sortedMap = new LinkedHashMap<BiochemicalReaction, Integer>();
		for (Iterator<Map.Entry<BiochemicalReaction, Integer>> it = list.iterator(); it.hasNext();) {
			Map.Entry<BiochemicalReaction, Integer> entry = it.next();
			sortedMap.put(entry.getKey(), entry.getValue());
		}
		return sortedMap;
	}
	
	public void draw(float x_){
		x = x_;
		checkBrushing();
		parent.textSize(13);
		parent.fill(150);
		parent.rect(x,0,w1,25);
		parent.fill(0);
		parent.textAlign(PApplet.CENTER);
		parent.text("Reaction",x+w1/2,18);
	
		if (hightlightList==null) return;
	
		x=x-800;
		
		
		int countLitems = 0;
		for (int i=0;i<hightlightList.length;i++){
			if (hightlightList[i]>=1){
				countLitems++;
			}
		}
		
		if (bPopup == true || b>=-1){
			// Compute positions
			float itemH2 = (parent.height-yBeginList)/(itemHash.size());
			if (itemH2>maxH)
				itemH2 =maxH;
			for (int i=0;i<itemHash.size();i++){
				iY[i].target(yBeginList+i*itemH2);
				iH[i].target(itemH2);
			}
			
			for (int i=0;i<itemHash.size();i++){
				iY[i].update();
				iH[i].update();
			}
		
		
			parent.fill(220,245);
			///parent.fill(255);
			parent.stroke(0,150);
			parent.rect(x-260, yBegin, w+1000,parent.height);
			
			
			
			float xRect = x+400;
			// Draw another button
			if (sAll){
				parent.noStroke();
				parent.fill(0);
				parent.rect(x+10,30,200,19);
				parent.fill(180);
			}
			else if (b==-1){
				parent.fill(255);
			}
			else{
				parent.fill(0);
			}
			parent.textSize(13);
			parent.textAlign(PApplet.CENTER);
			parent.text("All Reactions",xRect,45);
			
			int i=0;
			for (Map.Entry<BiochemicalReaction, Integer> entry : itemHash.entrySet()) {
				float textSixe = PApplet.map(iH[i].value, 0, maxH, 2, 13);
				parent.textSize(textSixe);
				
				if (i==s){
					parent.noStroke();
					parent.fill(0);
					parent.rect(xRect-10,iY[i].value-iH[i].value,w-25,iH[i].value);
					parent.fill(255,0,0);
				}
				else if (i==b){
					parent.fill(200,0,0);
				}
				else{
					parent.fill(0);
				}
				parent.textAlign(PApplet.CENTER);
				//parent.text(entry.getKey().getDisplayName(),xRect,iY[i].value-iH[i].value/4);
				float r = PApplet.map(PApplet.sqrt(entry.getValue()), 0, PApplet.sqrt(maxSize), 0, maxH/2);
				
				parent.noStroke();
				if (i==s){
					parent.fill(255,0,0);
				}
				else if (i==b){
					parent.fill(255);
				}
				else{
					parent.fill(0);
				}
				parent.ellipse(xRect,iY[i].value-iH[i].value/2, r, r);
				
				i++;
			}	
			
			// Draw proteins
			float xL = x;
			float xL2 = xL+200;
			float xR = x+800;
			float xR2 = xR-200;
			 for (int p=0; p<proteins.length;p++){
				iP[p].update();
			}
			
			parent.fill(0);
			parent.textSize(13);
			parent.textAlign(PApplet.CENTER);
			parent.text("Input Proteins", xL, 45);
			parent.text("Input Complexes", xL2, 45);
			parent.text("Output Complexes", xR2, 45);
			parent.text("Output Proteins", xR, 45);
			
			for (int p=0; p<proteins.length;p++){
				float y3 = iP[p].value;
				float textSixe = PApplet.map(hProtein, 0, maxH, 2, 13);
				parent.textSize(textSixe);
				
				parent.fill(0);
				if (main.MainMatrix.isSmallMolecule(proteins[p])){
					parent.fill(80);
					parent.textSize(textSixe*3/4f);
					
				}	
					
				parent.textAlign(PApplet.RIGHT);
				parent.text(proteins[p], xL,y3);
				
				parent.textAlign(PApplet.LEFT);
				parent.text(proteins[p], xR,y3);
		
			}
			drawReactions(xL, xL2, xRect, xR, xR2);
			checkGroup.draw((int) (xR+200), 50);
			
		}	
	}
	
	
	
	 
	// draw Reactions links
	public void drawReactions(float xL, float xL2, float xRect, float xR, float xR2) {
		int i2=0;
		float sat =200;
		for (Map.Entry<BiochemicalReaction, Integer> entry : itemHash.entrySet()) {
			 //for (BiochemicalReaction current : main.MainMatrix.reactionSet){
			//System.out.println("BiochemicalReaction "+(i2+1)+": "+current.getDisplayName());
			
			BiochemicalReaction rect = entry.getKey();
			Object[] s = rect.getLeft().toArray();
			  for (int i3=0;i3<s.length;i3++){
				  String name = main.MainMatrix.getProteinName(s[i3].toString());
				  if (name!=null){
					 // System.out.println("	Left "+(i3+1)+" = "+name);
					  int pIndex = getProteinIndex(name);
					  if (pIndex>=0){
						  parent.stroke(200,0,0,sat);
						  parent.line(xL, iP[pIndex].value-hProtein/4f, xRect, iY[i2].value-iH[i2].value/2);
					  }
					  else{
						  System.out.println("CAN NOT find protein = "+name+"	s[i3]="+s[i3]);
					  }
				  }	  
				  else{
					  if (s[i3].toString().contains("Complex")){
						//  System.out.println("	Left "+(i3+1)+" = "+s[i3]);
						  int id = main.MainMatrix.getComplex_RDFId_to_id(s[i3].toString());
						  ArrayList<String> components = main.MainMatrix.getAllGenesInComplexById(id);
						  
						  float yL2 = 0;
						  int numAvailableComponents = 0;
						  for (int k=0;k<components.size();k++){
							  int pIndex = getProteinIndex(components.get(k));
							  if (pIndex>=0){
								  yL2+= iP[pIndex].value-hProtein/4f;
								  numAvailableComponents++;
							  }	  
						  }
						  if (numAvailableComponents==0)
							  yL2 =iY[i2].value-iH[i2].value/2;
						  else 	  
							  yL2 /= numAvailableComponents;
						  for (int k=0;k<components.size();k++){
							//	 System.out.println("		 contains "+components.get(k));
							  int pIndex = getProteinIndex(components.get(k));
							  if (pIndex>=0){
								  parent.stroke(0,100,150,sat);
								  parent.line(xL, iP[pIndex].value-hProtein/4f, xL2, yL2);
							  }
						  }
						  parent.stroke(0,0,150,sat);
						  parent.line(xL2, yL2, xRect, iY[i2].value-iH[i2].value/2);
					  }
					  //else
					//	  System.out.println("	Left "+(i3+1)+" = "+s[i3]);
				  }
			  }

			  Object[] s2 = rect.getRight().toArray();
			  for (int i3=0;i3<s2.length;i3++){
				  String name = main.MainMatrix.getProteinName(s2[i3].toString());
				  if (name!=null){
					  //System.out.println("	Right "+(i3+1)+" = "+name);
					  int pIndex = getProteinIndex(name);
					  if (pIndex>=0){
						  parent.stroke(200,0,0);
						  parent.line(xRect, iY[i2].value-iH[i2].value/2,xR, iP[pIndex].value-hProtein/4f);
					  }
				  }
				  else{
					  if (s2[i3].toString().contains("Complex")){
					//	  System.out.println("	Right "+(i3+1)+" = "+s2[i3]);
						  int id = main.MainMatrix.getComplex_RDFId_to_id(s2[i3].toString());
						  ArrayList<String> components = main.MainMatrix.getAllGenesInComplexById(id);
						  float yR2 = 0;
						  int numAvailableComponents = 0;
						  for (int k=0;k<components.size();k++){
							  int pIndex = getProteinIndex(components.get(k));
							  if (pIndex>=0){
								  yR2+= iP[pIndex].value-hProtein/4f;
								  numAvailableComponents++;
							  }	  
						  }
						  if (numAvailableComponents==0)
							  yR2 =iY[i2].value-iH[i2].value/2;
						  else 	  
							  yR2 /= numAvailableComponents;
						  
						  parent.stroke(0,0,150,sat);
						  parent.line(xRect, iY[i2].value-iH[i2].value/2, xR2, yR2);
					
						  for (int k=0;k<components.size();k++){
							//	 System.out.println("		 contains "+components.get(k));
							  int pIndex = getProteinIndex(components.get(k));
							  if (pIndex>=0){
								  parent.stroke(0,100,150,sat);
								  parent.line(xR2, yR2, xR, iP[pIndex].value-hProtein/4f);
							  }
						  }
							
					  }
					 // else		
					//	  System.out.println("	Right "+(i3+1)+" = "+s2[i3]);
				  }
			  }
			// System.out.println("  		getLeft() = "+current.getLeft());
			// System.out.println("  		getRight() ="+ current.getRight());
			 i2++;
		 }
	 }
		
	
	public Map.Entry<BiochemicalReaction, Integer> getEntryHashId(int hashID) {
	 	 int i=0;
	 	 for (Map.Entry<BiochemicalReaction, Integer> entry : itemHash.entrySet()) {
			if (i==hashID){
				return entry;
			}
			i++;
		 }
		 return null;
	 }
	
	 public int getIndexSetByName(String name) {
	 	 int i=0;
		 for (Complex current : main.MainMatrix.complexSet){
			 if (current.getDisplayName().equals(name)){
				 return i;
			 }
		 }
		i++;		
		 return -33;
	 }
	 
	 public int getIndexHashByName(String name) {
	 	 int i=0;
		 for (Map.Entry<BiochemicalReaction, Integer> entry : itemHash.entrySet()) {
			 if (entry.getKey().getDisplayName().equals(name)){
			  return i;
			 }
		 }
		i++;		
		 return -11;
	 }
	 
	
	public int getIndexInSet(int brushing) {
		String name = "";
		int i=0;
		for (Map.Entry<BiochemicalReaction, Integer> entry : itemHash.entrySet()) {
			if (i==brushing){
				name = entry.getKey().getDisplayName();
			}
			i++;
		}	
		
		i=0;
		for (Complex current : main.MainMatrix.complexSet){
			if (current.getDisplayName().equals(name))
				return i;
			i++;
		}
		return -5;	
	}
	
	public int getIndexInHash(int indexSet) {
		int i=0;
		String name = "";
		for (Complex current : main.MainMatrix.complexSet){
			if (indexSet==i)
				name = current.getDisplayName();
			i++;
		}
		
		i=0;
		for (Map.Entry<BiochemicalReaction, Integer> entry : itemHash.entrySet()) {
			if (entry.getKey().getDisplayName().equals(name)){
				return i;
			}
			i++;
		}	
		return -5;	
	}
		
	 public void mouseClicked() {
		if (b==-1){
			sAll = !sAll;
		}
		else{
			if (b!=s)
				s = b;
			else
				s =-200;
		}
		
	}
	 
	public void checkBrushing() {
		if (itemHash==null || iH==null || iH.length==0) return;
		int mX = parent.mouseX;
		int mY = parent.mouseY;
		if (x<mX && mX<x+w1 && 0<=mY && mY<=yBegin){
			bPopup=true;
			return;
		}	
		else if (bPopup){
			if (x<mX && mX<x+w1 && yBegin<=mY && mY<=iY[0].value-iH[0].value){
				b=-1;
				return;
			}	
			for (int i=0; i<itemHash.size(); i++){
				if (x<=mX && mX<=x+w*2 && iY[i].value-iH[i].value<=mY && mY<=iY[i].value){
					b =i;
					hightlightList[i] = 1; 
					return;
				}	
			}
		}
		bPopup=false;		
		b =-100;
	}
	
}