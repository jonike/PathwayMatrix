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
	public static boolean sPopup = true;
	public static boolean bPopup = false;
	public static boolean sAll = false;
	public static int bRect = -1000;
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
	public ArrayList<Integer> bProteinLeft = new ArrayList<Integer>();
	public ArrayList<Integer> bProteinRight = new ArrayList<Integer>();
	public Integrator[] iP;
	
	public static CheckBox checkGroup;
	
	public float xL = x;
	public float xL2 = xL+200;
	public float xRect = x+400;
	public float xR = x+800;
	public float xR2 = xR-200;
	
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
			Object[] s = current.getLeft().toArray();
			
			// compute size of reaction
			int size = 0;
			for (int i3=0;i3<s.length;i3++){
				  String name = main.MainMatrix.getProteinName(s[i3].toString());
				  if (name!=null){
					  size++;
				  }	  
				  else if (s[i3].toString().contains("Complex")){
					  int id = main.MainMatrix.getComplex_RDFId_to_id(s[i3].toString());
					  ArrayList<String> components = main.MainMatrix.getAllGenesInComplexById(id);
					  size += components.size();
				  }
				  else 
					  size++;
			}
			 
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
	
	public void countProteinParticipation(){
		
		
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
		x = x_-800;
		xL = x;
		xL2 = xL+200;
		xRect = x+400;
		xR = x+800;
		xR2 = xR-200;
		
		checkBrushing();
		parent.textSize(13);
		parent.fill(150);
		if (bPopup)
			parent.stroke(255,0,0);
		parent.rect(x+800,0,w1,25);
		parent.fill(0);
		parent.textAlign(PApplet.CENTER);
		parent.text("Reaction",x+800+w1/2,18);
	
		if (hightlightList==null) return;
	
		
		
		int countLitems = 0;
		for (int i=0;i<hightlightList.length;i++){
			if (hightlightList[i]>=1){
				countLitems++;
			}
		}
		
		if (sPopup == true || bRect>=-1){
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
		
		
			parent.fill(230,250);
			///parent.fill(255);
			parent.stroke(0,150);
			parent.rect(x-260, yBegin, w+1000,parent.height);
			
			
			
			// Draw another button
			if (sAll){
				parent.noStroke();
				parent.fill(0);
				parent.rect(x+10,30,200,19);
				parent.fill(180);
			}
			else if (bRect==-1){
				parent.fill(255);
			}
			else{
				parent.fill(0);
			}
			parent.textSize(13);
			parent.textAlign(PApplet.CENTER);
			parent.text("All Reactions",xRect,45);
			
			// Draw proteins *****************************
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
				if (bRect>=0){
					
					// Get protein in the brushing reactions
					int i4=0;
					for (Map.Entry<BiochemicalReaction, Integer> entry : itemHash.entrySet()) {
						if (i4==bRect){
							BiochemicalReaction rect = entry.getKey();
							Object[] aLeft = rect.getLeft().toArray();
							Object[] aRight = rect.getRight().toArray();
							bProteinLeft = getProteinsInOneSideOfReaction(aLeft);
							bProteinRight = getProteinsInOneSideOfReaction(aRight);
						}
						i4++;
					}
					if (bProteinLeft.indexOf(p)>=0)
						drawProteinLeft(p,255);
					else
						drawProteinLeft(p,25);
					
					if (bProteinRight.indexOf(p)>=0)
						drawProteinRight(p,255);
					else
						drawProteinRight(p,25);
				}
				else{
					drawProteinLeft(p,200);
					drawProteinRight(p,200);
				}
			}
			
			
			// Reaction Links ******************
			int i2=0;
			for (Map.Entry<BiochemicalReaction, Integer> entry : itemHash.entrySet()) {
				BiochemicalReaction rect = entry.getKey();
				if (bRect>=0)
					drawReactionLink(rect, i2, xL, xL2, xRect, xR, xR2, 25);
				else 
					drawReactionLink(rect, i2, xL, xL2, xRect, xR, xR2, 200);
				i2++;
			}
			
			// Draw brushing reactions ***************
			if (bRect>=0){
				int i3=0;
				for (Map.Entry<BiochemicalReaction, Integer> entry : itemHash.entrySet()) {
					if (i3==bRect){
						BiochemicalReaction rect = entry.getKey();
						drawReactionLink(rect, i3, xL, xL2, xRect, xR, xR2, 255);
						break;
					}
					i3++;
				}
			}
			
			
			// Draw reaction Nodes **************************
			int i=0;
			for (Map.Entry<BiochemicalReaction, Integer> entry : itemHash.entrySet()) {
				if (bRect>=0)
					drawReactionNode(entry, i, 25);
				else
					drawReactionNode(entry, i, 200);
				i++;
			}	
			checkGroup.draw((int) (xR+200), 50);
		}	
	}
	public ArrayList<Integer> getProteinsInOneSideOfReaction(Object[] s) {
		ArrayList<Integer> a = new ArrayList<Integer>();
		for (int i3=0;i3<s.length;i3++){
			  String name = main.MainMatrix.getProteinName(s[i3].toString());
			  if (name!=null){
				  int pIndex = getProteinIndex(name);
				  if (pIndex>=0){
					  a.add(pIndex);
				  }
				  else{
					  System.out.println("CAN NOT find protein = "+name+"	s[i3]="+s[i3]);
				  }
			  }	  
			  else{
				  if (s[i3].toString().contains("Complex")){
					  int id = main.MainMatrix.getComplex_RDFId_to_id(s[i3].toString());
					  ArrayList<String> components = main.MainMatrix.getAllGenesInComplexById(id);
					  
					  for (int k=0;k<components.size();k++){
						  int pIndex = getProteinIndex(components.get(k));
						  if (pIndex>=0){
							  a.add(pIndex);
						  }	  
					  }
					 
				  }
				  //else
				//	  System.out.println("	Left "+(i3+1)+" = "+s[i3]);
			  }
		  }
		return a;
	}
		
	
	public void drawProteinLeft(int p, float sat) {
		float y3 = iP[p].value;
		float textSixe = PApplet.map(hProtein, 0, maxH, 2, 13);
		parent.textSize(textSixe);
		parent.fill(0,sat);
		if (main.MainMatrix.isSmallMolecule(proteins[p])){
			parent.fill(80,sat);
			parent.textSize(textSixe*3/4f);
			
		}	
		parent.textAlign(PApplet.RIGHT);
		parent.text(proteins[p], xL,y3);
	}
	
	public void drawProteinRight(int p, float sat) {
		float y3 = iP[p].value;
		float textSixe = PApplet.map(hProtein, 0, maxH, 2, 13);
		parent.textSize(textSixe);
		parent.fill(0,sat);
		if (main.MainMatrix.isSmallMolecule(proteins[p])){
			parent.fill(80,sat);
			parent.textSize(textSixe*3/4f);
			
		}	
		parent.textAlign(PApplet.LEFT);
		parent.text(proteins[p], xR,y3);
	}
		
	public void drawReactionNode(Map.Entry<BiochemicalReaction, Integer> entry, int i, float sat) {
		float r = PApplet.map(PApplet.sqrt(entry.getValue()), 0, PApplet.sqrt(maxSize), 0, maxH/2);
		parent.noStroke();
		parent.fill(0,sat);
		parent.ellipse(xRect,iY[i].value-iH[i].value/2, r, r);
		
		// draw brushing reaction name
		if (i==bRect){
			parent.fill(0);
			parent.ellipse(xRect,iY[i].value-iH[i].value/2, r, r);
			
			parent.fill(0);
			parent.textSize(13);
			parent.textAlign(PApplet.CENTER);
			float y3 = iY[i].value-iH[i].value;
			if (y3<55)
				y3=55;
			parent.text(entry.getKey().getDisplayName(),xRect,y3);
		}
	}
		 
	// draw Reactions links
	public void drawReactionLink(BiochemicalReaction rect, int i2, float xL, float xL2, float xRect, float xR, float xR2, float sat) {
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
					  parent.stroke(200,0,0,sat);
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
		 if (bPopup)
			 sPopup = !sPopup;
		if (bRect==-1){
			sAll = !sAll;
		}
		else{
			if (bRect!=s)
				s = bRect;
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
		else if (sPopup){
			bPopup=false;		
			
			if (x<mX && mX<x+w1 && yBegin<=mY && mY<=iY[0].value-iH[0].value){
				bRect=-1;
				return;
			}	
			for (int i=0; i<itemHash.size(); i++){
				if (xRect-50<=mX && mX<=xRect+50 && iY[i].value-iH[i].value<=mY && mY<=iY[i].value){
					bRect =i;
					hightlightList[i] = 1; 
					return;
				}	
			}
		}
		bPopup=false;		
		bRect =-100;
	}
	
}