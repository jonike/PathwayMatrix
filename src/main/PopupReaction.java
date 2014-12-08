package main;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.biopax.paxtools.model.level3.BiochemicalReaction;
import org.biopax.paxtools.model.level3.Complex;
import org.biopax.paxtools.model.level3.SmallMolecule;

import processing.core.PApplet;

public class PopupReaction{
	public static boolean sPopup = true;
	public static boolean bPopup = false;
	public static int bRect = -1000;
	public static ArrayList<Integer> sRectList = new ArrayList<Integer>();
	public static ArrayList<Integer> sRectListL = new ArrayList<Integer>();
	public static ArrayList<Integer> sRectListR = new ArrayList<Integer>();
	public PApplet parent;
	public float x = 0;
	public float xButton = 0;
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
	
	public static Map<BiochemicalReaction, Integer> rectHash =  new HashMap<BiochemicalReaction, Integer>();
	public static ArrayList<BiochemicalReaction> rectList =  new ArrayList<BiochemicalReaction>();
	public static ArrayList<String>[] rectWordList ;
	float itemH2 = 0; // height of items in the reaction
	
	public String[] proteins = null;
	public static  Map<String,Integer> mapProteinRDFId_index;
	
	public ArrayList<Integer> bProteinLeft = new ArrayList<Integer>();
	public ArrayList<Integer> bProteinRight = new ArrayList<Integer>();
	public Integrator[] iP;
	
	public static CheckBox check2;
	public static CheckBox check3;
	public static CheckBox check5;
	public static CheckBox check11;
	public static CheckBox check12;
	public static CheckBox check13;
	public static CheckBox check14;
	public static CheckBox check15;
	public static TextBox textbox1; 
	
	public float xL = x;
	public float xL2 = xL+200;
	public float xRect = x+400;
	public float xR = x+800;
	public float xR2 = xR-200;
	
	public static Color smallMoleculeColor = new Color(150,150,0);
	public static Color unidentifiedElementColor = new Color(130,70,130);
	public static Color formComplexColor = new Color(0,150,100);
	public static Color complexRectionColor = new Color(0,0,180);
	public static Color proteinRectionColor = new Color(180,0,0);
	
	public static WordCloud wordCloud;
	public static int numTop =30;
	public ArrayList<String> unidentifiedList = new ArrayList<String>();
	
	public ArrayList<Integer> complexList = new ArrayList<Integer>();
	public ArrayList<Integer> processedComplexLeft = new ArrayList<Integer>();
	public ArrayList<Integer> processedComplexRight = new ArrayList<Integer>();
	// Unidentified Elements
	public float yUFO = 0;
	
	public Integrator[] yComplexesL; 
	public float[] rComplexesL; 
	public Integrator[] yComplexesR; 
	public float[] rComplexesR; 
	
	public int bProteinL =-1;
	public int bProteinR =-1;
	public int bComplexL =-1;
	public int bComplexR =-1;
	
	// Reaction simulation
	public Integrator[][] iS;
	public Integrator[] iS1;
	public Integrator[] iS2;
	public Integrator[] iS3;
	public Integrator[] iS4;
	
	public int minSatSimulation =15;
	public int level1SatSimulation =50;
	public int stepSatSimulation =15;
	public static ArrayList<Integer> simulationRectList = new ArrayList<Integer>();
	public static ArrayList<Integer> simulationRectListLevel = new ArrayList<Integer>();
	public static ArrayList<String> interElements = new ArrayList<String>();
	public static ArrayList<Integer> interElementsLevel = new ArrayList<Integer>();

	ArrayList<Integer> proteinsDownStreamList = new ArrayList<Integer>();
	ArrayList<Integer> proteinsDownStreamList2 = new ArrayList<Integer>();
	ArrayList<Integer> levelDownStreamList = new ArrayList<Integer>();
	ArrayList<Integer> levelDownStreamList2 = new ArrayList<Integer>();
	ArrayList<Integer> processedList = new ArrayList<Integer>();
	
	ArrayList<Integer> loopReactionList = new ArrayList<Integer>();
	
	ArrayList<Integer> deleteReactionList = new ArrayList<Integer>();
	
	int deleteProtein =-1;
	Integrator iDelete =  new Integrator(0,0.1f,0.4f);
	
	public PopupReaction(PApplet parent_){
		parent = parent_;
		check2 = new CheckBox(parent, "Rearrange reactions");
		check3 = new CheckBox(parent, "Remove non-react proteins");
		check5 = new CheckBox(parent, "Display names");
		check11 = new CheckBox(parent, "Fade links of Small molecules");
		check12 = new CheckBox(parent, "Fade links of Unidentified elements");
		check13 = new CheckBox(parent, "Fade links of Complex formation");
		check14 = new CheckBox(parent, "Fade links of Complex reaction");
		check15 = new CheckBox(parent, "Fade links of Protein reaction");
		textbox1 = new TextBox(parent, "Search");
		wordCloud = new WordCloud(parent, 10,290,250,parent.height-250);
	}
	
	@SuppressWarnings("unchecked")
	public void setItems(){
		int i=0;
		maxSize =0;
		Map<BiochemicalReaction, Integer> unsortMap  =  new HashMap<BiochemicalReaction, Integer>();
		s=-400;
		for (BiochemicalReaction current : main.MainMatrixVersion_1_6.reactionSet){
			Object[] s = current.getLeft().toArray();
			
			// Compute size of reaction
			int size = 0;
			for (int i3=0;i3<s.length;i3++){
				  String name = main.MainMatrixVersion_1_6.getProteinName(s[i3].toString());
				  if (name!=null){
					  size++;
				  }	  
				  else if (main.MainMatrixVersion_1_6.mapComplexRDFId_index.get(s[i3].toString())!=null){
					  int id = main.MainMatrixVersion_1_6.mapComplexRDFId_index.get(s[i3].toString());
					  ArrayList<String> components = main.MainMatrixVersion_1_6.proteinsInComplex[id];
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
		rectHash = sortByComparator(unsortMap);
		rectList =  new ArrayList<BiochemicalReaction>();
		for (Map.Entry<BiochemicalReaction, Integer> entry : rectHash.entrySet()) {
			rectList.add(entry.getKey());
		}
			
		// Word cloud
		rectWordList =  new ArrayList[rectHash.size()];
		WordCount wc1 = new WordCount(numTop);
		ArrayList<String> a = new ArrayList<String>();
		int r=0;
		for (Map.Entry<BiochemicalReaction, Integer> entry : rectHash.entrySet()) {
			rectWordList[r] = new ArrayList<String>();
			String rectName = entry.getKey().getDisplayName();
			if (rectName!=null){
				String[] pieces = rectName.split(" ");
				for (int k=0;k<pieces.length;k++){
					String str = pieces[k].trim();
					a.add(str);
					rectWordList[r].add(str);
				}
			}
			r++;
		}
			
		wc1.countNames(a); 
		wordCloud.updateTags(wc1.wordArray, wc1.counts);
		
		// positions of items
		iX = new Integrator[rectHash.size()];
		iY = new Integrator[rectHash.size()];
		iH = new Integrator[rectHash.size()];
		iS = new Integrator[rectHash.size()][rectHash.size()];
		iS1 = new Integrator[rectHash.size()];
		iS2 = new Integrator[rectHash.size()];
		iS3 = new Integrator[rectHash.size()];
		iS4 = new Integrator[rectHash.size()];
		for (i=0;i<rectHash.size();i++){
			iX[i] = new Integrator(x, 0.5f,0.1f);
			iY[i] = new Integrator(20, 0.5f,0.1f);
			iH[i] = new Integrator(10, 0.5f,0.1f);
			for (int j=0;j<rectHash.size();j++){
				iS[i][j] = new Integrator(0, 0.5f,0.2f);
			}
			iS1[i] = new Integrator(0, 0.2f,1f);
			iS2[i] = new Integrator(0, 0.2f,1f);
			iS3[i] = new Integrator(0, 0.2f,1);
			iS4[i] = new Integrator(0, 0.2f,1f);
		}
		
		hightlightList =  new int[rectHash.size()];
		for (i=0;i<rectHash.size();i++){
			hightlightList[i] = -1;
		}
			
		int numValid = main.MainMatrixVersion_1_6.ggg.size();
		mapProteinRDFId_index = new HashMap<String,Integer>();
		for (int p=0; p<numValid;p++){
			mapProteinRDFId_index.put( main.MainMatrixVersion_1_6.ggg.get(p).name, p);
		}
		updateComplexList();
		updateUnidentifiedElements();
		int numInvalid = unidentifiedList.size();
		
		proteins =  new String[numValid+numInvalid];
		iP =  new Integrator[numValid+numInvalid];
		
		for (int p=0; p<numValid;p++){
			proteins[p] =  main.MainMatrixVersion_1_6.ggg.get(p).name;
			iP[p] =   new Integrator(20, 0.5f,0.1f);
		}
		for (int p=0; p<numInvalid;p++){
			proteins[numValid+p] =  unidentifiedList.get(p);
			mapProteinRDFId_index.put(unidentifiedList.get(p), numValid+p);
			iP[numValid+p] =   new Integrator(20, 0.5f,0.1f);
		}
		
		
		simulationRectList = new ArrayList<Integer>();
		simulationRectListLevel = new ArrayList<Integer>();
			
		
		updateProteinPositions();
		
	}
	
	public void updateComplexList(){
		complexList = new ArrayList<Integer>();
		int maxID = 0;
		for (int r=0;r<rectList.size();r++) {
			BiochemicalReaction rect = rectList.get(r);
			Object[] aLeft = rect.getLeft().toArray();
			Object[] aRight = rect.getRight().toArray();
			for (int i3=0;i3<aLeft.length;i3++){
				  if (main.MainMatrixVersion_1_6.mapComplexRDFId_index.get(aLeft[i3].toString())!=null){
					  int id = main.MainMatrixVersion_1_6.mapComplexRDFId_index.get(aLeft[i3].toString());
					  if (id>maxID)
						  maxID =id;
					  if(complexList.indexOf(id)<0)
						  complexList.add(id);
				  }
			}
			for (int i3=0;i3<aRight.length;i3++){
				  if (main.MainMatrixVersion_1_6.mapComplexRDFId_index.get(aRight[i3].toString())!=null){
					  int id = main.MainMatrixVersion_1_6.mapComplexRDFId_index.get(aRight[i3].toString());
					  if (id>maxID)
						  maxID =id;
					  if(complexList.indexOf(id)<0)
						  complexList.add(id);
				  }
			}
		}
		yComplexesL =  new Integrator[maxID+1];
		rComplexesL =  new float[maxID+1];
		yComplexesR =  new Integrator[maxID+1];
		rComplexesR =  new float[maxID+1];
		for (int i=0;i<yComplexesL.length;i++){
			yComplexesL[i] = new Integrator(10, 0.5f,0.1f);
			yComplexesR[i] = new Integrator(10, 0.5f,0.1f);
		}
		
	}
	
	public void updateUnidentifiedElements(){
		unidentifiedList = new ArrayList<String>();
		for (int r=0;r<rectList.size();r++) {
			BiochemicalReaction rect = rectList.get(r);
			Object[] aLeft = rect.getLeft().toArray();
			Object[] aRight = rect.getRight().toArray();
			
			ArrayList<String> a1 = getUnidentifiedElements2(aLeft);
			for (int i=0;i<a1.size();i++){
				String ufo = a1.get(i);
				if (!unidentifiedList.contains(ufo))
					unidentifiedList.add(ufo);
			}
			ArrayList<String> a2 = getUnidentifiedElements2(aRight);
			for (int i=0;i<a2.size();i++){
				String ufo = a2.get(i);
				if (!unidentifiedList.contains(ufo))
					unidentifiedList.add(ufo);
			}
		}
	//	System.out.println(unidentifiedList);
	}
	
	public ArrayList<String> getUnidentifiedElements2(Object[] s) {
		ArrayList<String> a = new ArrayList<String>();
		for (int i3=0;i3<s.length;i3++){
			  String name = main.MainMatrixVersion_1_6.getProteinName(s[i3].toString());
			  if (mapProteinRDFId_index.get(name)!=null){
			  }
			  else  if (main.MainMatrixVersion_1_6.mapComplexRDFId_index.get(s[i3].toString())!=null){
				  int id = main.MainMatrixVersion_1_6.mapComplexRDFId_index.get(s[i3].toString());
				  ArrayList<String> components = main.MainMatrixVersion_1_6.proteinsInComplex[id];
				  for (int k=0;k<components.size();k++){
					 if (mapProteinRDFId_index.get(components.get(k))==null){
						 a.add(components.get(k));
					 }	  
				  }
			  }
			  else{
				 a.add(s[i3].toString());
			 } 
		}
	//	System.out.println(a);
		return a;
	}
	
	public void updateProteinPositions(){
		if (check3.s){
			ArrayList<Integer> reactProteinList = new ArrayList<Integer>();
			
			for (int r=0;r<rectList.size();r++) {
				BiochemicalReaction rect = rectList.get(r);
				Object[] aLeft = rect.getLeft().toArray();
				Object[] aRight = rect.getRight().toArray();
				
				ArrayList<Integer> a1 = getProteinsInOneSideOfReaction(aLeft);
				for (int i=0;i<a1.size();i++){
					int ind = a1.get(i);
					if (reactProteinList.indexOf(ind)<0)
						reactProteinList.add(ind);
				}
				ArrayList<Integer> a2 = getProteinsInOneSideOfReaction(aRight);
				for (int i=0;i<a2.size();i++){
					int ind = a2.get(i);
					if (reactProteinList.indexOf(ind)<0)
						reactProteinList.add(ind);
				}
			}
			
			hProtein = (parent.height-yBeginList-2)/(reactProteinList.size());  // Save 20 pixels for Unidentified elements
			if (hProtein>maxH)
				hProtein =maxH;
			
			ArrayList<Integer> a = orderBySimilarity(reactProteinList);
			for (int p=0; p<proteins.length;p++){
				int index = a.indexOf(p);
				if (index>=0)
					iP[p].target(yBeginList+hProtein*index);
				else
					iP[p].target(parent.height+20);
			}
		}
		else{
			hProtein = (parent.height-yBeginList-2)/(proteins.length);  // Save 20 pixels for Unidentified elements
			if (hProtein>maxH)
				hProtein =maxH;
			for (int p=0; p<proteins.length;p++){
				int order = p;
				iP[p].target(yBeginList+hProtein*order);
			}
		}
		
		updateReactionPositions();  /// **********Update reactions when updating proteins **********
	}
	
	
	// Order by similarity 
	public ArrayList<Integer> orderBySimilarity(ArrayList<Integer> reactProteinList){	
		ArrayList<Integer> processedProteins =  new ArrayList<Integer>();
		int beginIndex = 0;
		processedProteins.add(beginIndex);
		
		int[][] scoresComplex = computeScoreComplex();
		int[][] scoresReaction = computeScoreReaction();
		while (true){
			int similarIndex =  getSimilarProtein(reactProteinList, processedProteins,scoresComplex,scoresReaction);
			if (similarIndex<0) break;
			processedProteins.add(similarIndex);
			beginIndex = similarIndex;
		}
		return processedProteins;
	}
	
	public int getSimilarProtein(ArrayList<Integer> reactProteinList, ArrayList<Integer> a, int[][] scoresComplex, int[][] scoresReaction){
		float maxScore = Float.NEGATIVE_INFINITY;
		int maxIndex = -1;
		for (int j=0;j<proteins.length;j++){
			if (reactProteinList.indexOf(j)<0) continue;
			if (a.contains(j)) continue;
			
			float sum = 0;
			for (int i=0;i<a.size();i++){
				int index1 = a.get(i);
				sum += scoresComplex[index1][j]*(a.size()-i)*10;
				sum += scoresReaction[index1][j]*(a.size()-i);
			}
			if (sum>maxScore){
				maxScore = sum;
				maxIndex = j;
			}
		}
		
		return maxIndex;
	}
	
	public int[][] computeScoreComplex(){
		int[][] score = new int [proteins.length][proteins.length];
		for (int c=0;c<complexList.size();c++){
			 ArrayList<String> components = main.MainMatrixVersion_1_6.proteinsInComplex[complexList.get(c)];
			 for (int k=0;k<components.size();k++){
				 int index1 = mapProteinRDFId_index.get(components.get(k));
				 for (int l=0;l<components.size();l++){
					 if (l==k) continue;
					 if (mapProteinRDFId_index.get(components.get(l))!=null){
						int index2 =  mapProteinRDFId_index.get(components.get(l));
						score[index1][index2]++;
					 }
					 else{
						 System.out.println(mapProteinRDFId_index+"&&&&&&&&&&&&&&"+components.get(l));
					 }
				 }	
			 }	
		}
		
		
		return score;
	}
		
	
	public int[][] computeScoreReaction(){
		int[][] score = new int [proteins.length][proteins.length];
		for (int r=0;r<rectList.size();r++) {
			BiochemicalReaction rect = rectList.get(r);
			Object[] aLeft = rect.getLeft().toArray();
			Object[] aRight = rect.getRight().toArray();
			
			ArrayList<Integer> a1 = getProteinsInOneSideOfReaction(aLeft);
			for (int i=0;i<a1.size();i++){
				int index1 = a1.get(i);
				for (int j=0;j<a1.size();j++){
					int index2 = a1.get(j);
					if (index1==index2 || index1<0 || index2<0) continue;
					score[index1][index2]++; 
					score[index2][index1]++; 
				}
			}
			
			ArrayList<Integer> a2 = getProteinsInOneSideOfReaction(aRight);
			for (int i=0;i<a2.size();i++){
				int index1 = a2.get(i);
				for (int j=0;j<a2.size();j++){
					int index2 = a2.get(j);
					if (index1==index2 || index1<0 || index2<0) continue;
					score[index1][index2]++; 
					score[index2][index1]++; 
				}
			}
			
			
			for (int i=0;i<a1.size();i++){
				int index1 = a1.get(i);
				for (int j=0;j<a2.size();j++){
					int index2 = a2.get(j);
					if (index1==index2 || index1<0 || index2<0) continue;
					score[index1][index2]++; 
					score[index2][index1]++; 
				}
			}
		}
		return score;
	}
	
		
	public void updateReactionPositions(){
		itemH2 = (parent.height-yBeginList)/(rectHash.size());
		// Compute positions
		if (itemH2>maxH)
			itemH2 =maxH;
		for (int i=0;i<rectHash.size();i++){
			iH[i].target(itemH2);
		}	
		
		if (check2.s){
			int indexOfItemHash=0;
			Map<Integer, Float> unsortMap  =  new HashMap<Integer, Float>();
			for (Map.Entry<BiochemicalReaction, Integer> entry : rectHash.entrySet()) {
				BiochemicalReaction rect = entry.getKey();
				Object[] aLeft = rect.getLeft().toArray();
				Object[] aRight = rect.getRight().toArray();
				ArrayList<Integer> proteinLeft = getProteinsInOneSideOfReaction(aLeft);
				ArrayList<Integer> proteinRight = getProteinsInOneSideOfReaction(aRight);
				
				float score = 0;
				float size = 0;
				for (int i=0; i<proteinLeft.size();i++){
					int pOrder = proteinLeft.get(i);
					if (pOrder>=0 && !main.MainMatrixVersion_1_6.isSmallMolecule(proteins[pOrder])) {// DO NOT order by small molecules
						score -= iP[pOrder].target;
						size++;
					}	
				}
				for (int i=0; i<proteinRight.size();i++){
					int pOrder = proteinRight.get(i);
					if (pOrder>=0 &&  !main.MainMatrixVersion_1_6.isSmallMolecule(proteins[pOrder])) {// DO NOT order by small molecules
						score -= iP[pOrder].target;
						size++;
					}	
				}
				
				if (size>0)
					score = score/size;
				
				unsortMap.put(indexOfItemHash, score);	
				indexOfItemHash++;
			}
			
			Map<Integer, Float> sortedMap = sortByComparator2(unsortMap);
			int i5 = 0;
			for (Map.Entry<Integer, Float> entry : sortedMap.entrySet()) {
				int rectOrder = entry.getKey();
				iY[rectOrder].target(yBeginList+i5*itemH2);
				i5++;
			}
		}
		else{
			for (int i=0;i<rectHash.size();i++){
				iY[i].target(yBeginList+i*itemH2);
			}
		}
		updateComplexPositions();
	}
	
	public void updateComplexPositions(){
		for (int r=0;r<rectList.size();r++) {
			BiochemicalReaction rect = rectList.get(r);
			Object[] aLeft = rect.getLeft().toArray();
			Object[] aRight = rect.getRight().toArray();
			for (int i3=0;i3<aLeft.length;i3++){
				  if (main.MainMatrixVersion_1_6.mapComplexRDFId_index.get(aLeft[i3].toString())!=null){
					  int id = main.MainMatrixVersion_1_6.mapComplexRDFId_index.get(aLeft[i3].toString());
					  ArrayList<String> components = main.MainMatrixVersion_1_6.proteinsInComplex[id];
					  float yL2 = 0;
					  int numAvailableComponents = 0;
					  for (int k=0;k<components.size();k++){
						  if (mapProteinRDFId_index.get(components.get(k))!=null){
							  yL2+= iP[mapProteinRDFId_index.get(components.get(k))].target-hProtein/4f;
							  numAvailableComponents++;
						  }	  
					  }
					  if (numAvailableComponents==0)
						  yL2 =iY[r].target-iH[r].target/2;
					  else 	  
						  yL2 /= numAvailableComponents;
					  
					  float radius = PApplet.map(PApplet.sqrt(components.size()), 0, PApplet.sqrt(maxSize), 0, maxH/2);
						 
					  yComplexesL[id].target(yL2);
					  rComplexesL[id] = radius;
					
				  }
			}
			for (int i3=0;i3<aRight.length;i3++){
				  if (main.MainMatrixVersion_1_6.mapComplexRDFId_index.get(aRight[i3].toString())!=null){
					  int id = main.MainMatrixVersion_1_6.mapComplexRDFId_index.get(aRight[i3].toString());
					  ArrayList<String> components = main.MainMatrixVersion_1_6.proteinsInComplex[id];
					  float yR2 = 0;
				      int numAvailableComponents = 0;
					  for (int k=0;k<components.size();k++){
						  if (mapProteinRDFId_index.get(components.get(k))!=null){
							  yR2+= iP[mapProteinRDFId_index.get(components.get(k))].target-hProtein/4f;
							  numAvailableComponents++;
						  }	  
					  }
					  if (numAvailableComponents==0)
						  yR2 =iY[r].target-iH[r].target/2;
					  else 	  
						  yR2 /= numAvailableComponents;
					  
					  
					  float radius = PApplet.map(PApplet.sqrt(components.size()), 0, PApplet.sqrt(maxSize), 0, maxH/2);
					  yComplexesR[id].target(yR2);
					  rComplexesR[id] = radius;
				  }
			}	  
			
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
	
	// Sort Reactions by score (average positions of proteins)
	public static Map<Integer, Float> sortByComparator2(Map<Integer, Float> unsortMap) {
		// Convert Map to List
		List<Map.Entry<Integer, Float>> list = 
			new LinkedList<Map.Entry<Integer, Float>>(unsortMap.entrySet());
 
		// Sort list with comparator, to compare the Map values
		Collections.sort(list, new Comparator<Map.Entry<Integer, Float>>() {
			public int compare(Map.Entry<Integer, Float> o1,
                                           Map.Entry<Integer, Float> o2) {
				return -(o1.getValue()).compareTo(o2.getValue());
			}
		});
 
		// Convert sorted map back to a Map
		Map<Integer, Float> sortedMap = new LinkedHashMap<Integer, Float>();
		for (Iterator<Map.Entry<Integer, Float>> it = list.iterator(); it.hasNext();) {
			Map.Entry<Integer, Float> entry = it.next();
			sortedMap.put(entry.getKey(), entry.getValue());
		}
		return sortedMap;
	}
	
	
	
	public void drawButton(float x_){
		xButton = x_;
		
		checkBrushing();
		parent.textSize(12);
		parent.fill(150);
		if (bPopup)
			parent.stroke(255,0,0);
		if (sPopup)
			parent.fill(0);
		parent.rect(xButton,0,w1,25);
		parent.fill(0);
		if (sPopup)
			parent.fill(255);
		parent.textAlign(PApplet.CENTER);
		parent.text("Reaction",xButton+w1/2,18);
	
		if (hightlightList==null) return;
	
		int countLitems = 0;
		for (int i=0;i<hightlightList.length;i++){
			if (hightlightList[i]>=1){
				countLitems++;
			}
		}
		
		
	}
	
	
	public void drawReactions(float x_){
		xL = x_;
		float www = parent.width/2f;
		xL2 = xL+www/6;
		xRect = x_+www/2;
		xR = x_+www;
		xR2 = xR-www/6;
		
		// Draw seach box
		textbox1.draw(xRect);
		
		if (proteins==null) return;
			for (int i=0;i<rectHash.size();i++){
				iY[i].update();
				iH[i].update();
			}
			
			
			// Draw proteins *****************************
			float maxY = 0;
			for (int p=0; p<proteins.length;p++){
				iP[p].update();
				if (iP[p].value>maxY)
					maxY = iP[p].value;
			}
			yUFO = maxY+20;
			  		
			
			
			
			bProteinLeft =  new ArrayList<Integer>();
			bProteinRight =  new ArrayList<Integer>();
			if (bRect>=0){
				BiochemicalReaction rect = rectList.get(bRect);
				Object[] aLeft = rect.getLeft().toArray();
				Object[] aRight = rect.getRight().toArray();
				bProteinLeft = getProteinsInOneSideOfReaction(aLeft);
				bProteinRight = getProteinsInOneSideOfReaction(aRight);
			}	
			else if (!textbox1.searchText.equals("")){
				for (int r=0;r<sRectList.size();r++) {
					BiochemicalReaction rect = rectList.get(sRectList.get(r));
					Object[] aLeft = rect.getLeft().toArray();
					Object[] aRight = rect.getRight().toArray();
					
					ArrayList<Integer> a1 = getProteinsInOneSideOfReaction(aLeft);
					for (int i=0;i<a1.size();i++){
						int ind = a1.get(i);
						if (bProteinLeft.indexOf(ind)<0)
							bProteinLeft.add(ind);
					}
					ArrayList<Integer> a2 = getProteinsInOneSideOfReaction(aRight);
					for (int i=0;i<a2.size();i++){
						int ind = a2.get(i);
						if (bProteinRight.indexOf(ind)<0)
							bProteinRight.add(ind);
					}
				}
			}
			else if (sRectListL.size()>0){
				for (int r=0;r<sRectListL.size();r++) {
					BiochemicalReaction rect = rectList.get(sRectListL.get(r));
					Object[] aLeft = rect.getLeft().toArray();
					Object[] aRight = rect.getRight().toArray();
					
					ArrayList<Integer> a1 = getProteinsInOneSideOfReaction(aLeft);
					for (int i=0;i<a1.size();i++){
						int ind = a1.get(i);
						if (bProteinLeft.indexOf(ind)<0)
							bProteinLeft.add(ind);
					}
					ArrayList<Integer> a2 = getProteinsInOneSideOfReaction(aRight);
					for (int i=0;i<a2.size();i++){
						int ind = a2.get(i);
						if (bProteinRight.indexOf(ind)<0)
							bProteinRight.add(ind);
					}
				}
			}
			else if (sRectListR.size()>0){
				for (int r=0;r<sRectListR.size();r++) {
					BiochemicalReaction rect = rectList.get(sRectListR.get(r));
					Object[] aLeft = rect.getLeft().toArray();
					Object[] aRight = rect.getRight().toArray();
					
					ArrayList<Integer> a1 = getProteinsInOneSideOfReaction(aLeft);
					for (int i=0;i<a1.size();i++){
						int ind = a1.get(i);
						if (bProteinLeft.indexOf(ind)<0)
							bProteinLeft.add(ind);
					}
					ArrayList<Integer> a2 = getProteinsInOneSideOfReaction(aRight);
					for (int i=0;i<a2.size();i++){
						int ind = a2.get(i);
						if (bProteinRight.indexOf(ind)<0)
							bProteinRight.add(ind);
					}
				}
			}
			
			
			// Draw Protein names ***************************************************
			if (main.MainMatrixVersion_1_6.buttonCausality.s && simulationRectList.size()>0){
				for (int p=0; p<proteins.length;p++){
					drawProteinLeft(p,20);
					drawProteinRight(p,20);
				}
				
				for (int i=0;i<simulationRectList.size();i++){
					int r = simulationRectList.get(i);
					
					int currentLevel = simulationRectListLevel.get(simulationRectListLevel.size()-1);
					int rectIndex = simulationRectList.indexOf(r);
					int rLevel = simulationRectListLevel.get(rectIndex);
					if (rLevel==currentLevel){
						BiochemicalReaction rect = rectList.get(r);
						ArrayList<Integer> lList = getProteinsInOneSideOfReaction(rect.getLeft().toArray());
						for (int j=0; j<lList.size();j++){
							int protein = lList.get(j);
							drawProteinLeft(protein,255);
							
						}
						ArrayList<Integer> rList = getProteinsInOneSideOfReaction(rect.getRight().toArray());
						for (int j=0; j<rList.size();j++){
							int protein = rList.get(j);
							drawProteinRight(protein,255);
							
						}
						
						
					}
				}
				
			}
			else if (bRect>=0 || !textbox1.searchText.equals("") || sRectListL.size()>0 || sRectListR.size()>0){
				for (int p=0; p<proteins.length;p++){
					// Get protein in the brushing reactions
					if (bProteinLeft.indexOf(p)>=0)
						drawProteinLeft(p,255);
					else
						drawProteinLeft(p,25);
					
					if (bProteinRight.indexOf(p)>=0)
						drawProteinRight(p,255);
					else
						drawProteinRight(p,25);
				}
			}
			else{
				for (int p=0; p<proteins.length;p++){
					drawProteinLeft(p,200);
					drawProteinRight(p,200);
				}
			}
			
			
			// Compute brushing complexes ******************
			sRectListL = new ArrayList<Integer>();
			sRectListR = new ArrayList<Integer>();
			int bComplexLold = bComplexL;
			bComplexL=-1;
			for (int c=0;c<yComplexesL.length;c++){
				if (PApplet.dist(xL2,yComplexesL[c].value, parent.mouseX, parent.mouseY)<=rComplexesL[c]){
					bComplexL = c;
					for (int r=0;r<rectList.size();r++) {
						BiochemicalReaction rect = rectList.get(r);
						Object[] sLeft = rect.getLeft().toArray();
						for (int i3=0;i3<sLeft.length;i3++){
							  if (main.MainMatrixVersion_1_6.mapComplexRDFId_index.get(sLeft[i3].toString())!=null){
								  int id = main.MainMatrixVersion_1_6.mapComplexRDFId_index.get(sLeft[i3].toString());
								  if (id==c && !sRectListL.contains(r))
									  sRectListL.add(r);
							  }	
						}	  
					 }
					break; // Only allow to brushing 1 complex
				 }
			}
			if (bComplexLold!=bComplexL){
				resetIntegrators();
			}
			int bComplexRold = bComplexR;
			bComplexR=-1;
			for (int c=0;c<yComplexesR.length;c++){
				if (PApplet.dist(xR2,yComplexesR[c].value, parent.mouseX, parent.mouseY)<=rComplexesR[c]){
					bComplexR = c;
					for (int r=0;r<rectList.size();r++) {
						BiochemicalReaction rect = rectList.get(r);
						Object[] sRight = rect.getRight().toArray();
						for (int i3=0;i3<sRight.length;i3++){
							  if (main.MainMatrixVersion_1_6.mapComplexRDFId_index.get(sRight[i3].toString())!=null){
								  int id = main.MainMatrixVersion_1_6.mapComplexRDFId_index.get(sRight[i3].toString());
								  if (id==c && !sRectListR.contains(r))
									  sRectListR.add(r);
							  }	
						}	  
					 }
					break; // Only allow to brushing 1 complex
				 }
			}
			if (bComplexRold!=bComplexR){
				resetIntegrators();
			}
			
			// Brushing proteins *******************************************************************
			if (sRectListL.size()==0){
				int bProteinLold = bProteinL;
				bProteinL =-1;
				for (int p=0; p<proteins.length;p++){
					if (xL-80<=parent.mouseX && parent.mouseX<= xL &&
							iP[p].value-hProtein*2/3<=parent.mouseY && parent.mouseY<=iP[p].value+hProtein*1/3){
						bProteinL =p;
						break;
					}	
				}
				
				for (int r=0;r<rectList.size();r++) {
					BiochemicalReaction rect = rectList.get(r);
					Object[] sLeft = rect.getLeft().toArray();
					for (int i3=0;i3<sLeft.length;i3++){
						 String name = main.MainMatrixVersion_1_6.getProteinName(sLeft[i3].toString());
						  if (name==null)
							  name = sLeft[i3].toString();
						  if (mapProteinRDFId_index.get(name)!=null && mapProteinRDFId_index.get(name)==bProteinL){
							  if (!sRectListL.contains(r))
								  sRectListL.add(r);
						  }
						  /*
						  else  if (main.MainMatrixVersion_1_6.mapComplexRDFId_index.get(sLeft[i3].toString())!=null){
							  int id = main.MainMatrixVersion_1_6.mapComplexRDFId_index.get(sLeft[i3].toString());
							  ArrayList<String> components = main.MainMatrixVersion_1_6.proteinsInComplex[id];
							  for (int k=0;k<components.size();k++){
								  if (mapProteinRDFId_index.get(components.get(k))!=null){
									  if(mapProteinRDFId_index.get(components.get(k))==bProteinL){
										  sRectListL.add(r);
										  break;
									  }	  
								  }		
							  }
								  
						  }*/
					}	  
				 }
				if (bProteinLold!=bProteinL){
					resetIntegrators();
				}
			}
		    if (sRectListR.size()==0){
		    	int bProteinRold = bProteinR;
				bProteinR =-1;
				for (int p=0; p<proteins.length;p++){
					if (xR<=parent.mouseX && parent.mouseX<= xR+80 &&
							iP[p].value-hProtein<=parent.mouseY && parent.mouseY<=iP[p].value){
						bProteinR =p;
						break;
					}	
				}
				
				for (int r=0;r<rectList.size();r++) {
					BiochemicalReaction rect = rectList.get(r);
					Object[] sRight = rect.getRight().toArray();
					for (int i3=0;i3<sRight.length;i3++){
						 String name = main.MainMatrixVersion_1_6.getProteinName(sRight[i3].toString());
						  if (name==null)
							  name = sRight[i3].toString();
						  if (mapProteinRDFId_index.get(name)!=null && mapProteinRDFId_index.get(name)==bProteinR){
							  if (!sRectListR.contains(r))
								  sRectListR.add(r);
						  }
						  /*
						  else  if (main.MainMatrixVersion_1_6.mapComplexRDFId_index.get(sRight[i3].toString())!=null){
							  int id = main.MainMatrixVersion_1_6.mapComplexRDFId_index.get(sRight[i3].toString());
							  ArrayList<String> components = main.MainMatrixVersion_1_6.proteinsInComplex[id];
							  for (int k=0;k<components.size();k++){
								  if (mapProteinRDFId_index.get(components.get(k))!=null){
									  if(mapProteinRDFId_index.get(components.get(k))==bProteinR){
										  sRectListR.add(r);
										  break;
									  }	  
								  }		
							  }
								  
						  }*/
						
					}	  
				 }
				if (bProteinRold!=bProteinR){
					resetIntegrators();
				}
			}
			
			
			// Reaction Links ******************
			processedComplexLeft =  new ArrayList<Integer>();
			processedComplexRight =  new ArrayList<Integer>();
			
			for (int r=0;r<rectList.size();r++) {
				iS1[r].update();
				iS2[r].update();
				iS3[r].update();
				iS4[r].update();
			}
			
			if (main.MainMatrixVersion_1_6.buttonDelete.s ){
					
			}
			else if (main.MainMatrixVersion_1_6.buttonLoop.s ){
				
			}
			else if (simulationRectList.size()>0 ){
				for (int r=0;r<simulationRectList.size();r++) {
					BiochemicalReaction rect = rectList.get(simulationRectList.get(r));
					drawReactionLink(rect, simulationRectList.get(r), xL, xL2, xRect, xR, xR2, 255);
				}
			}
			else if (bRect>=0){
				for (int r=0;r<rectList.size();r++) {
					BiochemicalReaction rect = rectList.get(r);
					if (r==bRect) // Draw brushing reactions ***************
						drawReactionLink(rect, r, xL, xL2, xRect, xR, xR2, 255);
					else 
						drawReactionLink(rect, r, xL, xL2, xRect, xR, xR2, 25);
				}
			}
			else if (!textbox1.searchText.equals("") ){
				for (int r=0;r<rectList.size();r++) {
					BiochemicalReaction rect = rectList.get(r);
					if (sRectList.indexOf(r)>=0)
						drawReactionLink(rect, r, xL, xL2, xRect, xR, xR2, 255);
					else
						drawReactionLink(rect, r, xL, xL2, xRect, xR, xR2, 25);
				}
			}
			else if (sRectListL.size()>0){
				for (int r=0;r<rectList.size();r++) {
					BiochemicalReaction rect = rectList.get(r);
					if (sRectListL.indexOf(r)>=0)
						drawReactionLink(rect, r, xL, xL2, xRect, xR, xR2, 255);
					else
						drawReactionLink(rect, r, xL, xL2, xRect, xR, xR2, 25);
				}
			}
			else if (sRectListR.size()>0){
				for (int r=0;r<rectList.size();r++) {
					BiochemicalReaction rect = rectList.get(r);
					if (sRectListR.indexOf(r)>=0){
						drawReactionLink(rect, r, xL, xL2, xRect, xR, xR2, 255);
					}	
					else
						drawReactionLink(rect, r, xL, xL2, xRect, xR, xR2, 25);
				}
			}
			else{
				if (!main.MainMatrixVersion_1_6.buttonCausality.s && simulationRectList.size()==0){
					for (int r=0;r<rectList.size();r++) {
						BiochemicalReaction rect = rectList.get(r);
						drawReactionLink(rect, r, xL, xL2, xRect, xR, xR2, 200);
					}
				}
			}
			
			
			
			
			
			// Draw SHORTEST PATH ******************************************************************************
			if (bProteinL>=0){
				proteinsDownStreamList = new ArrayList<Integer>();
				proteinsDownStreamList2 = new ArrayList<Integer>();
				levelDownStreamList = new ArrayList<Integer>();
				levelDownStreamList2 = new ArrayList<Integer>();
				processedList = new ArrayList<Integer>();
				for (int r=0;r<rectList.size();r++) {
					BiochemicalReaction rect = rectList.get(r);
					Object[] sLeft = rect.getLeft().toArray();
					for (int i3=0;i3<sLeft.length;i3++){
						 String name = main.MainMatrixVersion_1_6.getProteinName(sLeft[i3].toString());
						  if (name==null)
							  name = sLeft[i3].toString();
						  if (mapProteinRDFId_index.get(name)!=null && mapProteinRDFId_index.get(name)==bProteinL){
							  processedList.add(r);
							  listProteinDownStream(r,0,proteinsDownStreamList, levelDownStreamList, proteinsDownStreamList2, levelDownStreamList2, processedList);
						  }
					}	  
				 }
				int currentLevel = -10;
				if (simulationRectListLevel!=null && simulationRectListLevel.size()>0)
					currentLevel = simulationRectListLevel.get(simulationRectListLevel.size()-1);
				
				for (int i=0;i<proteinsDownStreamList.size();i++){
					int p=proteinsDownStreamList.get(i);
					int level=levelDownStreamList.get(i);
					float y3 = iP[p].value;
					parent.fill(proteinRectionColor.getRGB());
					if (level==currentLevel+1){
						float sat = (parent.frameCount*20)%200;
						parent.fill(proteinRectionColor.getRed(), proteinRectionColor.getGreen(), proteinRectionColor.getBlue(),sat);
							
					}
					for (SmallMolecule currentMolecule : main.MainMatrixVersion_1_6.smallMoleculeSet){
						if (proteins[p].equals(currentMolecule.getDisplayName())){
							parent .fill(smallMoleculeColor.getRGB());
							if (level==currentLevel+1){
								float sat = (parent.frameCount*20)%200;
								parent.fill(smallMoleculeColor.getRed(), smallMoleculeColor.getGreen(), smallMoleculeColor.getBlue(),sat);
									
							}
						}
					}
						
					
					
					parent.textSize(12);
					parent.textAlign(PApplet.RIGHT);
					parent.text(level,xR-15,y3);
				
					float sat = (parent.frameCount*20)%200;
					parent .fill(200,0,0, 55+sat);
					parent.textAlign(PApplet.LEFT);
					//parent.text(proteins[p],xR,y3);
					
				}
				for (int i=0;i<proteinsDownStreamList2.size();i++){
					int p=proteinsDownStreamList2.get(i);
					int level=levelDownStreamList2.get(i);
					float y3 = iP[p].value;
					parent.fill(formComplexColor.getRGB());
					if (level==currentLevel+1){
						float sat = (parent.frameCount*20)%200;
						parent.fill(formComplexColor.getRed(), formComplexColor.getGreen(), formComplexColor.getBlue(),sat);
							
					}
					
					parent.textSize(12);
					parent.textAlign(PApplet.RIGHT);
					parent.text(level,xR-5,y3);
				
					float sat = (parent.frameCount*20)%200;
					parent .fill(0,0,200, 55+sat);
					parent.textAlign(PApplet.LEFT);
				//	parent.text(proteins[p],xR,y3);
				}
					
			}
			
			
			// Draw reaction causation ******************************************************************************
			loopReactionList =new ArrayList<Integer>();
			
			if (main.MainMatrixVersion_1_6.buttonDelete.s){
				iDelete.target(1);
				iDelete.update();
			
				
				ArrayList<Integer> processedList = new ArrayList<Integer>();
				for (int r=0;r<rectList.size();r++) {
					processedList.add(r);
					drawDownStreamReaction(r,-100, processedList, 1000);
				}
				
			}
			else if (simulationRectList.size()>0){
				ArrayList<Integer> processedList = new ArrayList<Integer>();
				for (int r=0;r<simulationRectList.size();r++){
					int rect = simulationRectList.get(r);
					int level = simulationRectListLevel.get(r);
					processedList.add(rect);
					drawDownStreamReaction(rect, level,processedList, iS4[rect].value);
				}
			}
			else if (main.MainMatrixVersion_1_6.buttonLoop.s){
				if (bRect>=0){
					ArrayList<Integer> loopList =  new ArrayList<Integer>();
					drawLoopFrom(bRect, loopList);
					loopReactionList = loopList;
				}
				else{
					for (int r=0;r<rectList.size();r++) {
						ArrayList<Integer> loopRectList =  new ArrayList<Integer>();
						drawLoopFrom(r, loopRectList);
						for (int j=0;j<loopRectList.size();j++){
							int index = loopRectList.get(j);
							if (loopReactionList.indexOf(index)<0)
								loopReactionList.add(index);
						}
					}
					
				}
				
			}	
			else if (main.MainMatrixVersion_1_6.buttonCausality.s){
				if (bRect>=0){
					ArrayList<Integer> processedList = new ArrayList<Integer>();
					processedList.add(bRect);
					drawDownStreamReaction(bRect,-100, processedList, 1000);
					
				}
				else{
					ArrayList<Integer> processedList = new ArrayList<Integer>();
					for (int r=0;r<rectList.size();r++) {
						processedList.add(r);
						drawDownStreamReaction(r,-100, processedList, 1000);
					}
				}
			}
			else{
				for (int r=0;r<rectList.size();r++) {
					for (int g=0;g<rectList.size();g++) {
						iS[r][g].set(0);
					}	
				}
			}
			
			
			
			// Draw reaction Nodes *************************************************************
			if (main.MainMatrixVersion_1_6.buttonDelete.s ){
				int i=0;
				for (Map.Entry<BiochemicalReaction, Integer> entry : rectHash.entrySet()) {
					if (deleteReactionList.indexOf(i)>=0)
						drawReactionNode(entry, i, 50+150*(1-iDelete.value));
					else
						drawReactionNode(entry, i, 200);
					i++;
				}
			}
			else if(main.MainMatrixVersion_1_6.buttonLoop.s){
				int i=0;
				for (Map.Entry<BiochemicalReaction, Integer> entry : rectHash.entrySet()) {
					if (loopReactionList.indexOf(i)>=0)
						drawReactionNode(entry, i, 255);
					else
						drawReactionNode(entry, i, 50);
					i++;
				}
			}
			else if (simulationRectList.size()>0){
				int r = 0;
				for (Map.Entry<BiochemicalReaction, Integer> entry : rectHash.entrySet()) {
					if (simulationRectList.indexOf(r)>=0)
						drawReactionNodeSimulation(entry, r);
						
					r++;
				}
			}
			else{
				int i=0;
				for (Map.Entry<BiochemicalReaction, Integer> entry : rectHash.entrySet()) {
					if (bRect>=0)
						drawReactionNode(entry, i, 25);
					else
						drawReactionNode(entry, i, 200);
					
					
					i++;
				}
			}
			
			float x7 = (xR+220);
			float y7 = 60;
			
			
			parent.strokeWeight(1);
			check5.draw((int) x7, (int) y7-19);
			check3.draw((int) x7, (int) y7);
			check2.draw((int) x7, (int) y7+19);
			
			
			if (main.MainMatrixVersion_1_6.buttonDelete.s){
				float x2 = parent.width*3/4f;
				float y2 = 180;
				parent.fill(0,20);
				parent.noStroke();
				parent.rect(x2-30, y2-40, parent.width-x2+50, parent.height-y2+50);
				
				parent.fill(0);
				parent.textSize(12);
				parent.text("Affected reactions: "+deleteReactionList.size()+" / "+rectList.size()+" biochemical reactions", x2-25,y2-20);
				
				for (int i=0;i<deleteReactionList.size();i++){
					int r = deleteReactionList.get(i);
					parent.fill(0);
					String name = rectList.get(r).getDisplayName();
					
					
					parent.text(name, x2 +25,y2+20*i);
				}

			}
			else if (main.MainMatrixVersion_1_6.buttonLoop.s){
				
			}
					
			else if (main.MainMatrixVersion_1_6.buttonCausality.s){
				float x2 = parent.width*3/4f;
				float y3 = 150;
				float y2 = 450;
				parent.fill(0,20);
				parent.noStroke();
				parent.rect(x2-30, y3-40, parent.width-x2+50, parent.height-y3+50);
				
				// Print out Reaction list
				parent.fill(0);
				parent.textSize(12);
				parent.text("Reaction list: ", x2-25,y2-20);
				
				boolean flashing = false;
				for (int i=0;i<simulationRectList.size();i++){
					int r = simulationRectList.get(i);
					parent.fill(0);
					String name = rectList.get(r).getDisplayName();
					
					int currentLevel = simulationRectListLevel.get(simulationRectListLevel.size()-1);
					int rectIndex = simulationRectList.indexOf(r);
					int rLevel = simulationRectListLevel.get(rectIndex);
					
					if (rLevel==currentLevel && iS4[r].value<990){
						float sat = 55+(parent.frameCount*20)%200;
						parent .fill(sat,0,0);
						flashing = true;
					}
					parent.text(name, x2+rLevel*25,y2+20*i);
					
				}
				
				// Print out intermediate proteins and complexes
				parent.fill(0);
				parent.textSize(12);
				parent.text("Intermediate proteins/complexes: ", x2-25,y3-20);
				for (int i=0;i<interElements.size();i++){
					String ref = interElements.get(i);
					parent.fill(0);
					
					
					
					String name = main.MainMatrixVersion_1_6.getProteinName(ref);
					  if (name==null){
						  String[] pieces = ref.split("/");
						  if (pieces.length>=1)
								name = pieces[pieces.length-1];
						}	  
					  if (mapProteinRDFId_index.get(name)!=null){
						  name = proteins[mapProteinRDFId_index.get(name)];
							
					  }	  
					  else if (main.MainMatrixVersion_1_6.mapComplexRDFId_index.get(ref)!=null){
						  int id = main.MainMatrixVersion_1_6.mapComplexRDFId_index.get(ref);
						  name = main.MainMatrixVersion_1_6.proteinsInComplex[id].toString();
						  
					  }
						
					int currentLevel = interElementsLevel.get(interElementsLevel.size()-1);
					int eLevel = interElementsLevel.get(i);
					
					if (eLevel==currentLevel && flashing){
						float sat = 55+ (parent.frameCount*20)%200;
						parent .fill(sat,0,0);
					}
					parent.text(name, x2+(eLevel)*25,y3+20*i);
					
				}
				
			}
			else{
				check11.draw((int) x7, (int) y7+44);
				check13.draw((int) x7, (int) y7+63);
				check14.draw((int) x7, (int) y7+82);
				check15.draw((int) x7, (int) y7+101);
				// Draw word cloud
				wordCloud.x1=parent.width-200; 
				wordCloud.x2=parent.width; 
				wordCloud.draw(parent);
				
				// Draw word relationship
				int[][] rel =  new int[numTop][numTop];
				for (int r=0;r<rectList.size();r++){
					for (int m=0;m<numTop;m++){
						for (int n=0;n<numTop;n++){
							if (wordCloud.words[m].equals("") || wordCloud.words[n].equals("")) 
								continue;
							if (rectWordList[r].contains(wordCloud.words[m].word) && rectWordList[r].contains(wordCloud.words[n].word))
								rel[m][n]++;
						}
					}		
				}
				drawRelationship(wordCloud, rel, Color.BLACK);
			}
			parent.fill(0);
			parent.textSize(13);
			parent.textAlign(PApplet.CENTER);
			parent.text(rectHash.size()+" Reactions",xRect,45);
			
			parent.fill(0);
			parent.textSize(13);
			parent.textAlign(PApplet.CENTER);
			parent.text("Input Proteins", xL, 45);
			parent.fill(complexRectionColor.getRGB());
			parent.text("Input Complexes", xL2, 45);
			parent.text("Output Complexes", xR2, 45);
			parent.fill(0);
			parent.text("Output Proteins", xR, 45);
	}

	public void drawLoopFrom(int beginReaction, ArrayList<Integer> loopRectList){
		ArrayList<Integer> downstreamList = new ArrayList<Integer>();
		ArrayList<Integer> parentList = new ArrayList<Integer>();
		ArrayList<Integer> levelDownstreamList = new ArrayList<Integer>();
		listReactionDownStream(beginReaction,0,downstreamList, parentList,levelDownstreamList);
		if (downstreamList.indexOf(beginReaction)>=0){
			int indexLastReaction = downstreamList.indexOf(beginReaction);
			int parent = parentList.get(indexLastReaction);
			ArrayList<Integer> a = new ArrayList<Integer>();
			while (parent!=beginReaction){
				a.add(parent);
				indexLastReaction = downstreamList.indexOf(parent);
				parent = parentList.get(indexLastReaction);
			}
			a.add(parent);
			for (int i=a.size()-1;i>=0;i--){
				int index = a.get(i);
				loopRectList.add(index);
			}
		}
		
		// Draw loop
		for (int i=0;i<loopRectList.size();i++){
			int r = loopRectList.get(i);
			int g = -1;
			if (i<loopRectList.size()-1)
				g = loopRectList.get(i+1);
			else{
				g = loopRectList.get(0);
			}
			drawArc2(r,g);
		}
		
	}
		
	public void listProteinDownStream(int r, int recursive, ArrayList<Integer> downstreamList,ArrayList<Integer> levelDownStreamList, ArrayList<Integer> downstreamList2,ArrayList<Integer> levelDownStreamList2, ArrayList<Integer> processedList){
		BiochemicalReaction rectSelected = rectList.get(r);
		Object[] sRight1 = rectSelected.getRight().toArray();
		// List output protein in current reaction
		 for (int i3=0;i3<sRight1.length;i3++){
			  String name = main.MainMatrixVersion_1_6.getProteinName(sRight1[i3].toString());
			  if (name==null)
				  name = sRight1[i3].toString();
			  if (mapProteinRDFId_index.get(name)!=null){
				  int pId = mapProteinRDFId_index.get(name);
				  if (downstreamList.indexOf(pId)<0){
					  downstreamList.add(pId);
					  levelDownStreamList.add(recursive+1);
				  }	  
			  }	
			  else if (main.MainMatrixVersion_1_6.mapComplexRDFId_index.get(name)!=null){
					  int id = main.MainMatrixVersion_1_6.mapComplexRDFId_index.get(name);
					  ArrayList<String> components = main.MainMatrixVersion_1_6.proteinsInComplex[id];
					  for (int i=0;i<components.size();i++){
						  int pId = mapProteinRDFId_index.get(components.get(i));
						  if (downstreamList2.indexOf(pId)<0){
							  downstreamList2.add(pId);
							  levelDownStreamList2.add(recursive+1);
						  }	 
					  }
			  }
		 }
		for (int g=0;g<rectList.size();g++) {
			if(g==r || processedList.indexOf(g)>=0) continue;
			BiochemicalReaction rect2 = rectList.get(g);
			Object[] sLeft2 = rect2.getLeft().toArray();
			ArrayList<String> commonElements = compareInputOutput(sRight1, sLeft2);
			if (commonElements.size()>0){
				processedList.add(g);
				listProteinDownStream(g, recursive+1,downstreamList,levelDownStreamList,downstreamList2,levelDownStreamList2,  processedList);
			}
		}
	}
	
	public void listReactionDownStream(int r, int recursive, ArrayList<Integer> downstreamList, ArrayList<Integer> parentList,ArrayList<Integer> levelDownStreamList){
		BiochemicalReaction rectSelected = rectList.get(r);
		Object[] sRight1 = rectSelected.getRight().toArray();
		
		// List current reaction
		for (int g=0;g<rectList.size();g++) {
			if(g==r) continue;
			BiochemicalReaction rect2 = rectList.get(g);
			Object[] sLeft2 = rect2.getLeft().toArray();
			ArrayList<String> commonElements = compareInputOutput(sRight1, sLeft2);
			if (commonElements.size()>0){
				if (downstreamList.indexOf(g)<0){
					downstreamList.add(g);
					parentList.add(r);
					levelDownStreamList.add(recursive+1);
					listReactionDownStream(g, recursive+1,downstreamList, parentList, levelDownStreamList);
				}	
				
			}
		}
	}
	
	public void drawDownStreamReaction(int r, int recursive, ArrayList<Integer> processedList, float iProcess){
		BiochemicalReaction rectSelected = rectList.get(r);
		Object[] sRight1 = rectSelected.getRight().toArray();
		for (int g=0;g<rectList.size();g++) {
			if(g==r) continue;
			BiochemicalReaction rect2 = rectList.get(g);
			Object[] sLeft2 = rect2.getLeft().toArray();
			ArrayList<String> commonElements = compareInputOutput(sRight1, sLeft2);
			if (commonElements.size()>0){
				if (iProcess>990){
					iS[r][g].target(1000);
					iS[r][g].update();
					drawArc(r,g, iS[r][g], recursive);
					if (recursive>=0){
						if (processedList.indexOf(g)<0 && iS[r][g].value>=990){
							processedList.add(r);
							if (simulationRectList.indexOf(g)<0){
								simulationRectList.add(g);
								simulationRectListLevel.add(recursive+1);
								
								for (int i=0;i<commonElements.size();i++){
									String str = commonElements.get(i);
									interElements.add(str);
									interElementsLevel.add(recursive+1);	
								}
							}	
						}
					}
				}
			}
			
		}
	}
	
	public void resetIntegrators(){
		for (int r=0;r<rectList.size();r++) {
			iS1[r].set(0);
			iS1[r].target(0);
			iS2[r].set(0);
			iS2[r].target(0);
			iS3[r].set(0);
			iS3[r].target(0);
			iS4[r].set(0);
			iS4[r].target(0);
		}
		
	}
	
	public float getReactionSaturation(int r){
		int levelDif = 0;
		  //System.out.println(simulationRectList.toString()+"   "+i2);
		  // System.out.println(simulationRectListLevel.toString()+"	"+iS2[i2].value);
		  if (simulationRectList.indexOf(r)>=0){
			  int levelIndex = simulationRectList.indexOf(r);
			  levelDif = simulationRectListLevel.get(simulationRectListLevel.size()-1)-simulationRectListLevel.get(levelIndex);
		  }
		  int newSat = 255;
		  if (levelDif>0){
			  newSat =  level1SatSimulation-(levelDif-1)*stepSatSimulation;
			  if (newSat<minSatSimulation)
				  newSat = minSatSimulation;
		  }
		  return newSat;
	}
		
	public void drawArc(int r, int g, Integrator inter, int level){
		float y1 = iY[r].value-iH[r].value/2;
		float y2 = iY[g].value-iH[g].value/2;
		float yy = (y1+y2)/2;
		
		
		float d = PApplet.abs(y2-y1);
		int numSec = (int) d;
		if (numSec==0) return;
		float beginAngle = PApplet.PI/2;
		if (y1>y2)
			beginAngle = -PApplet.PI/2;
		
		for (int k=0;k<=numSec;k++){
			float percent = inter.value/1000;
			float endAngle = beginAngle+PApplet.PI/numSec;
			if ((float) k/numSec >=(1-percent)){
				parent.noFill();
				float sss = (float) k/numSec;
				sss = PApplet.pow(sss,0.75f);
				
				float sat = 255-220*sss;
				float red = 255;
				float green = sss*255;
				float blue = 255-255*sss;
				
				parent.stroke(red,green,blue,sat);
				parent.strokeWeight(3);
				if(deleteReactionList.indexOf(r)>=0){
					red = red*(1-iDelete.value);
					green = green*(1-iDelete.value);
					blue = blue*(1-iDelete.value);
					
					if (red<0) red=0;
					if (green<0) green=0;
					if (blue<0) blue=0;
					
					parent.stroke(red,green,blue,sat);
					parent.strokeWeight(1.5f+1.5f*(1-iDelete.value));
					
				}		
				
				parent.arc(xRect, yy, d,d, beginAngle, endAngle);
			}
			beginAngle = endAngle;
			parent.strokeWeight(1);
		}
	}
	
	public void drawArc2(int r, int g){
		float y1 = iY[r].value-iH[r].value/2;
		float y2 = iY[g].value-iH[g].value/2;
		float yy = (y1+y2)/2;
		
		
		float d = PApplet.abs(y2-y1);
		int numSec = (int) d;
		if (numSec==0) return;
		float beginAngle = PApplet.PI/2;
		if (y1>y2)
			beginAngle = -PApplet.PI/2;
		
		for (int k=0;k<=numSec;k++){
			float percent = 1;
			float endAngle = beginAngle+PApplet.PI/numSec;
			if ((float) k/numSec >=(1-percent)){
				parent.noFill();
				float sss = (float) k/numSec;
				sss = PApplet.pow(sss,0.75f);
				parent.stroke(255,sss*255,255-255*sss, 220-200*sss);
				parent.strokeWeight(2);
				
				parent.arc(xRect, yy, d,d, beginAngle, endAngle);
			}
			beginAngle = endAngle;
			
		}
	}
		
	
	public ArrayList<String> compareInputOutput(Object[] a, Object[] b){
		ArrayList<String> results = new ArrayList<String>();
		for (int i=0; i<a.length;i++){
			String str1 = a[i].toString();
			for (int j=0; j<b.length;j++){
				String str2 = b[j].toString();
				if (str1.equals(str2)){
					 String name = main.MainMatrixVersion_1_6.getProteinName(str1);
					 if (!main.MainMatrixVersion_1_6.isSmallMolecule(name)){
						 results.add(str1);
					 }	 
				}	
			}
		}
		return results;
	}
		
	
	public void drawRelationship(WordCloud wc, int[][] rel, Color color){
		int max = 0;
		for (int i=0;i<numTop;i++){
			for (int j=i+1;j<numTop;j++){
				if (rel[i][j]>max)
					max = rel[i][j];
			}
		}	
		int brushing = wc.b;
		 for (int i=0;i<numTop;i++){
			if (i==brushing) continue;  // skip drawing the brushing relations
			float y1 = wc.words[i].y-wc.words[i].font_size/3f;
			for (int j=i+1;j<numTop;j++){
				float y2 = wc.words[j].y-wc.words[j].font_size/3f;
				float xx = wc.x1;
				float yy = (y1+y2)/2;
				parent.noFill();
				
				float maxWeight = max;
				if (max<=2){
					maxWeight = 3;
				}
				float wei = PApplet.map(rel[i][j], 0, maxWeight, 0, 100);
				parent.stroke(color.getRed(),color.getGreen(),color.getBlue(),wei);
				parent.strokeWeight(wei/20);
				parent.arc(xx, yy, y2-y1,y2-y1, PApplet.PI/2, 3*PApplet.PI/2);
			}
		}
		// Draw relationship of brushing term
		 if (brushing>=0){
		 	float y1 = wc.words[brushing].y-wc.words[brushing].font_size/3f;
			for (int j=0;j<numTop;j++){
				if (j==brushing) continue;
				float y2 = wc.words[j].y-wc.words[j].font_size/3f;
				float xx = wc.x1;
				float yy = (y1+y2)/2;
				parent.noFill();
				
				float maxWeight = max;
				if (max<=2){
					maxWeight = 3;
				}
				if (j>brushing && rel[brushing][j]>0){
					float wei = PApplet.map(rel[brushing][j], 0, maxWeight, 0, 150);
					parent.stroke(255,0,0,wei+155);
					parent.strokeWeight(wei/20);
					parent.arc(xx, yy, y2-y1,y2-y1, PApplet.PI/2, 3*PApplet.PI/2);
				}
				else if (j<brushing && rel[j][brushing]>0){
					float wei = PApplet.map(rel[j][brushing], 0, maxWeight, 0, 150);
					parent.stroke(255,0,0,wei+155);
					parent.strokeWeight(wei/20);
					parent.arc(xx, yy, y1-y2,y1-y2, PApplet.PI/2, 3*PApplet.PI/2);
				}
			}
		 } 
	}
	
	public  void drawGradientLine(float x1, float y1, float x2, float y2, Color color, float sat) {
		float gap = PApplet.abs(x2-x1)/4;
		
		parent.noStroke();
		
		for (float x = 0; x <= gap; x=x+1) {
			  float x3 = x1+x;	
			  float x4 = x2-x;	
			  float y3 = (x3-x1)*(y2-y1)/(float) (x2-x1) +y1;
			  float y4 = (x4-x1)*(y2-y1)/(float) (x2-x1) +y1;
			  //float dis1 = PApplet.min(x3-x1, x2-x3);
			  float alpha = PApplet.map(x, 0,gap, sat, 0);
			  if (alpha>1){
				   Color c = new Color(color.getRed(), color.getGreen(), color.getBlue(),(int) alpha);
				   parent.fill(c.getRGB());
				   parent.ellipse(x3,y3,1.25f,1.25f);
				   parent.ellipse(x4,y4,1.25f,1.25f);
			  } 
		}
	} 
	
	public ArrayList<Integer> getProteinsInOneSideOfReaction(Object[] s) {
		ArrayList<Integer> a = new ArrayList<Integer>();
		for (int i3=0;i3<s.length;i3++){
			  String name = main.MainMatrixVersion_1_6.getProteinName(s[i3].toString());
			  if (name==null)
				  name = s[i3].toString();
			  if (mapProteinRDFId_index.get(name)!=null){
				  a.add(mapProteinRDFId_index.get(name));
			  }
			  else  if (main.MainMatrixVersion_1_6.mapComplexRDFId_index.get(s[i3].toString())!=null){
				  int id = main.MainMatrixVersion_1_6.mapComplexRDFId_index.get(s[i3].toString());
				  ArrayList<String> components = main.MainMatrixVersion_1_6.proteinsInComplex[id];
				  for (int k=0;k<components.size();k++){
					  if (mapProteinRDFId_index.get(components.get(k))!=null){
						  a.add(mapProteinRDFId_index.get(components.get(k)));
					  }	  
					  else{
						  System.out.println("getProteinsInOneSideOfReaction: -----SOMETHING WRONG");
						  int reverseIndex = -1-unidentifiedList.indexOf(components.get(k));
						  a.add(reverseIndex);
					  }
				  }
			  }
			  else if (unidentifiedList.contains(s[i3].toString())){
				    int reverseIndex = -1-unidentifiedList.indexOf(s[i3].toString());
					a.add(reverseIndex);
			  }
			  else{
				  System.out.println("getProteinsInOneSideOfReaction: CAN NOT FIND ="+s[i3]+"-----SOMETHING WRONG");
			 } 
		  }
		return a;
	}
		
	
	public void drawProteinLeft(int p, float sat) {
		float y3 = iP[p].value;
		float textSixe = PApplet.map(hProtein, 0, maxH, 2, 12);
		parent.textSize(textSixe);
		String name = proteins[p];
		Color c =  new Color(0,0,0);
		if (main.MainMatrixVersion_1_6.isSmallMolecule(proteins[p])){
			c = smallMoleculeColor;
		}
		else if (unidentifiedList.contains(proteins[p])){
			c = unidentifiedElementColor;
			String[] pieces = name.split("/");
			if (pieces.length>1)
				name = pieces[pieces.length-1];
		}
		
		if (sat>=255 && textSixe<10)
			parent.textSize(10);
		if (sat==255 && p==bProteinL)
			parent.fill(c.getRed(), c.getGreen(), c.getBlue(), (parent.frameCount*22)%255);
		else
			parent.fill(c.getRed(), c.getGreen(), c.getBlue(),sat);
		parent.textAlign(PApplet.RIGHT);
		parent.text(name, xL,y3);
	}
	
	public void drawProteinRight(int p, float sat) {
		float y3 = iP[p].value;
		float textSixe = PApplet.map(hProtein, 0, maxH, 2, 12);
		parent.textSize(textSixe);
		String name = proteins[p];
		Color c =  new Color(0,0,0);
		if (main.MainMatrixVersion_1_6.isSmallMolecule(proteins[p])){
			c = smallMoleculeColor;
		}
		else if (unidentifiedList.contains(proteins[p])){
			c = unidentifiedElementColor;
			String[] pieces = name.split("/");
			if (pieces.length>1)
				name = pieces[pieces.length-1];
		}
		
		if (sat>=255 && textSixe<10)
			parent.textSize(10);
		if (sat==255 && p==bProteinR)
			parent.fill(c.getRed(), c.getGreen(), c.getBlue(), (parent.frameCount*22)%255);
		else
			parent.fill(c.getRed(), c.getGreen(), c.getBlue(),sat);
		
		parent.textAlign(PApplet.LEFT);
		parent.text(name, xR,y3);
		
	}
		
	public void drawReactionNode(Map.Entry<BiochemicalReaction, Integer> entry, int i, float sat) {
		float r = PApplet.map(PApplet.sqrt(entry.getValue()), 0, PApplet.sqrt(maxSize), 0, maxH/2);
		parent.noStroke();
		parent.fill(0,sat);
		
		String rectName = entry.getKey().getDisplayName();
		if (!textbox1.searchText.equals("")){
			if (sRectList.indexOf(i)>=0){
				parent.fill(100,0,0);
				parent.ellipse(xRect,iY[i].value-iH[i].value/2, r, r);
			}
		}	
		else if (sRectListL.size()>0){
			if (sRectListL.indexOf(i)>=0){
				parent.fill(100,0,0);
				parent.ellipse(xRect,iY[i].value-iH[i].value/2, r, r);
			}
		}
		else if (sRectListR.size()>0){
			if (sRectListR.indexOf(i)>=0){
				parent.fill(100,0,0);
				parent.ellipse(xRect,iY[i].value-iH[i].value/2, r, r);
			}
		}
		else 
			parent.ellipse(xRect,iY[i].value-iH[i].value/2, r, r);
		
		// Draw brushing reaction name
		if (i==bRect || (!textbox1.searchText.equals("") && sRectList.indexOf(i)>=0) || (sRectListL.size()>0 && sRectListL.indexOf(i)>=0) || (sRectListR.size()>0 && sRectListR.indexOf(i)>=0)){
			parent.fill(0);
			parent.ellipse(xRect,iY[i].value-iH[i].value/2, r, r);
			
			parent.fill(0);
			parent.textSize(12);
			parent.textAlign(PApplet.CENTER);
			float y3 = iY[i].value-iH[i].value*2/3;
			if (y3<55)
				y3=55;
			if (check5.s)
				parent.text(rectName,xRect,y3);
		}
	}
	
	public void drawReactionNodeSimulation(Map.Entry<BiochemicalReaction, Integer> entry, int r) {
		float radus = PApplet.map(PApplet.sqrt(entry.getValue()), 0, PApplet.sqrt(maxSize), 0, maxH/2);
		parent.noStroke();
		parent.fill(0);
		
		int currentLevel = simulationRectListLevel.get(simulationRectListLevel.size()-1);
		int rectIndex = simulationRectList.indexOf(r);
		int rLevel = simulationRectListLevel.get(rectIndex);
		if (rLevel==currentLevel && iS4[r].value<990){
			float sat = (parent.frameCount*20)%200;
			parent .fill(sat,0,0);
			float aditionalR = sat/50f;
			parent.ellipse(xRect,iY[r].value-iH[r].value/2, radus+ aditionalR, radus+aditionalR);
		}
		else{	
			parent.ellipse(xRect,iY[r].value-iH[r].value/2, radus, radus);
		}
		// Draw brushing reaction name
		if (check5.s || (rLevel==currentLevel &&  iS4[r].value<990)){
			parent.fill(0);
			parent.textSize(12);
			parent.textAlign(PApplet.CENTER);
			float y3 = iY[r].value-iH[r].value*4/5;
			String rectName = entry.getKey().getDisplayName();
			if (y3<55)
				y3=55;
			parent.text(rectName,xRect,y3);
		}
	}
		 
	// draw Reactions links
	public void drawReactionLink(BiochemicalReaction rect, int i2, float xL, float xL2, float xRect, float xR, float xR2, float sat) {
		Object[] sLeft = rect.getLeft().toArray();
		
		float newSatForSimulation = getReactionSaturation(i2);
		boolean isContainedComplexL =false; // checking if there is a complex;
		  for (int i3=0;i3<sLeft.length;i3++){
			  String name = main.MainMatrixVersion_1_6.getProteinName(sLeft[i3].toString());
			  if (name==null)
				  name = sLeft[i3].toString();

			  if (mapProteinRDFId_index.get(name)!=null){
				  float y5 = iP[mapProteinRDFId_index.get(name)].value-hProtein/4f;
				  float y6 = iY[i2].value-iH[i2].value/2;
				  if (check11.s && main.MainMatrixVersion_1_6.isSmallMolecule(name) && sat==200)
					  drawGradientLine(xL, y5, xRect, y6, smallMoleculeColor, sat);
				  else if (check15.s && !main.MainMatrixVersion_1_6.isSmallMolecule(name) && sat==200){
					  drawGradientLine(xL, y5, xRect, y6, proteinRectionColor, sat);
				  }
				  else  {
					  
					  if (sat==255){ // Draw simulation lines
						  if (iS1[i2].value>=990){
							  
							  iS2[i2].target(1000);
						  }
						  else{
							  iS2[i2].set(0);
						  }
						  float percent = iS2[i2].value/1000;
						  float xDel = (xRect-xL)*percent;
						  float yDel = (y6-y5)*percent;
						  
						  parent.stroke(proteinRectionColor.getRed(),proteinRectionColor.getGreen(),proteinRectionColor.getBlue(),newSatForSimulation);
						  if (main.MainMatrixVersion_1_6.isSmallMolecule(name)){
								parent.stroke(smallMoleculeColor.getRed(),smallMoleculeColor.getGreen(),smallMoleculeColor.getBlue(),newSatForSimulation);
						  }
						  parent.line(xL, y5, xL+xDel, y5+yDel);
					  }
					  else  {
						  parent.stroke(proteinRectionColor.getRed(),proteinRectionColor.getGreen(),proteinRectionColor.getBlue(),sat);
					  	  if (main.MainMatrixVersion_1_6.isSmallMolecule(name)){
								parent.stroke(smallMoleculeColor.getRed(),smallMoleculeColor.getGreen(),smallMoleculeColor.getBlue(),sat);
						  }
						  parent.line(xL, y5, xRect, y6);
					  }	  
				  }
			  }	  
			  else if (main.MainMatrixVersion_1_6.mapComplexRDFId_index.get(sLeft[i3].toString())!=null){
				  int id = main.MainMatrixVersion_1_6.mapComplexRDFId_index.get(sLeft[i3].toString());
				  
				  ArrayList<String> components = main.MainMatrixVersion_1_6.proteinsInComplex[id];
				  yComplexesL[id].update();
				  float yL2 = yComplexesL[id].value;
				  if (processedComplexLeft.indexOf(id)<0 || sat==255){  // if not drawn yet
					  if (processedComplexLeft.indexOf(id)<0)
							  processedComplexLeft.add(id);
					  for (int k=0;k<components.size();k++){
						  isContainedComplexL = true;
						  parent.stroke(formComplexColor.getRed(), formComplexColor.getGreen(), formComplexColor.getBlue(),sat);
						  if (mapProteinRDFId_index.get(components.get(k))!=null){
							  float y4 = iP[mapProteinRDFId_index.get(components.get(k))].value-hProtein/4f;
							  if (check13.s && sat==200)
								  drawGradientLine(xL, y4, xL2, yL2, formComplexColor, sat);
							  else{
								  if (sat==255){ // Draw simulation lines
									  iS1[i2].target(1000);
									  float percent = iS1[i2].value/1000;
									  float xDel = (xL2-xL)*percent;
									  float yDel = (yL2-y4)*percent;
									  parent.stroke(formComplexColor.getRed(), formComplexColor.getGreen(), formComplexColor.getBlue(),newSatForSimulation);
									  parent.line(xL, y4, xL+xDel, y4+yDel);
								  }
								  else{
									  parent.line(xL, y4, xL2, yL2);
								  }	  
							  }	  
						  }	
						  else{
							  if (check12.s && sat==200)
								  drawGradientLine(xL, yUFO, xL2, yL2, unidentifiedElementColor, sat);
							  else{
								  parent.stroke(unidentifiedElementColor.getRed(),unidentifiedElementColor.getGreen(),unidentifiedElementColor.getBlue(),sat);
								  parent.line(xL, yUFO, xL2, yL2);
							  }
						  }
					  }
					  
					  // Draw complex node
					  parent.noStroke();
					  if ( iS1[i2].value>=990)
						  parent.fill(complexRectionColor.getRed(),complexRectionColor.getGreen(),complexRectionColor.getBlue(),newSatForSimulation);
					  else
						  parent.fill(complexRectionColor.getRed(),complexRectionColor.getGreen(),complexRectionColor.getBlue(),sat);
					  parent. pushMatrix();
					  parent.translate(xL2, yL2);
					  polygon(0, 0, rComplexesL[id]/2+1, 4); 
					  parent.popMatrix();
					  
					  if (sat==255 && check5.s){
						  parent.textAlign(PApplet.CENTER);
						  parent.textSize(12);
						  parent.text(main.MainMatrixVersion_1_6.complexList.get(id).getDisplayName(),xL2,yL2-5);
					  }
				  }
				  float yRect2 = iY[i2].value-iH[i2].value/2;
				  if (check14.s && sat==200)
					  drawGradientLine(xL2, yL2, xRect, yRect2, complexRectionColor, sat);
				  else{	
					  if (sat==255){ // Draw simulation lines
						  if (iS1[i2].value>=990){
							  iS2[i2].target(1000);
						  }
						  else{
							  iS2[i2].set(0);
						  }
						  float percent = iS2[i2].value/1000;
						  float xDel = (xRect-xL2)*percent;
						  float yDel = (yRect2-yL2)*percent;
						  parent.stroke(complexRectionColor.getRed(),complexRectionColor.getGreen(),complexRectionColor.getBlue(),newSatForSimulation);
						  parent.line(xL2, yL2, xL2+xDel, yL2+yDel);
					  }
					  else{
						  parent.stroke(complexRectionColor.getRed(),complexRectionColor.getGreen(),complexRectionColor.getBlue(),sat);
						  parent.line(xL2, yL2, xRect, yRect2);
					  }	  
				  }
			  }
			  else if (unidentifiedList.contains(sLeft[i3].toString())){
				  float y5 = iY[i2].value-iH[i2].value/2;
				  if (check12.s && sat==200)
					  drawGradientLine(xL, yUFO, xRect, y5, unidentifiedElementColor, sat);
				  else{
					  parent.stroke(unidentifiedElementColor.getRed(),unidentifiedElementColor.getGreen(),unidentifiedElementColor.getBlue(),sat);
					  parent.line(xL, yUFO, xRect, y5);
				  }
			  }
			  else{
				//System.out.println("drawReactionLink Left: CAN NOT FIND ="+sLeft[i3]);
			  }
		  }
		  if (!isContainedComplexL)
			  iS1[i2].target(1000);

		  boolean isContainedComplexR =false; // checking if there is a complex;
		  Object[] sRight = rect.getRight().toArray();
		  for (int i3=0;i3<sRight.length;i3++){
			  String name = main.MainMatrixVersion_1_6.getProteinName(sRight[i3].toString());
			  if (name==null)
				  name = sRight[i3].toString();
			  if (mapProteinRDFId_index.get(name)!=null){
				  float y5 = iY[i2].value-iH[i2].value/2;
				  float y6 = iP[mapProteinRDFId_index.get(name)].value-hProtein/4f;
				  if (check11.s && main.MainMatrixVersion_1_6.isSmallMolecule(name) &&sat==200)
					  drawGradientLine(xRect, y5, xR, y6, smallMoleculeColor, sat);
				  else if (check15.s && !main.MainMatrixVersion_1_6.isSmallMolecule(name) && sat==200){
					  drawGradientLine(xRect, y5, xR, y6, proteinRectionColor, sat);
				  }
						
				  else{
					   if (sat==255){ // Draw simulation lines
						  if (iS2[i2].value>=990){
							  iS3[i2].target(1000);
						  }
						  else{
							  iS3[i2].set(0);
						  }
							
						  float percent = iS3[i2].value/1000;
						  float xDel = (xR-xRect)*percent;
						  float yDel = (y6-y5)*percent;
						  parent.stroke(proteinRectionColor.getRed(),proteinRectionColor.getGreen(),proteinRectionColor.getBlue(),newSatForSimulation);
						  if (main.MainMatrixVersion_1_6.isSmallMolecule(name))
								parent.stroke(smallMoleculeColor.getRed(),smallMoleculeColor.getGreen(),smallMoleculeColor.getBlue(),newSatForSimulation);
						  parent.line(xRect, y5, xRect+xDel, y5+yDel);
						  
					  }
					  else{
						  parent.stroke(proteinRectionColor.getRed(),proteinRectionColor.getGreen(),proteinRectionColor.getBlue(),sat);
						  if (main.MainMatrixVersion_1_6.isSmallMolecule(name))
								parent.stroke(smallMoleculeColor.getRed(),smallMoleculeColor.getGreen(),smallMoleculeColor.getBlue(),sat);
						 parent.line(xRect, y5,xR, y6);
					  }	  
				  }	  
			  }
			  else if (main.MainMatrixVersion_1_6.mapComplexRDFId_index.get(sRight[i3].toString())!=null){
				  int id = main.MainMatrixVersion_1_6.mapComplexRDFId_index.get(sRight[i3].toString());
				  ArrayList<String> components = main.MainMatrixVersion_1_6.proteinsInComplex[id];
				  yComplexesR[id].update();
				  float yR2 = yComplexesR[id].value;
				 
				  
				  float yRect2 = iY[i2].value-iH[i2].value/2;
				  if (check14.s && sat==200)
					  drawGradientLine(xRect, yRect2, xR2, yR2, complexRectionColor, sat);
				  else{	  
					  if (sat==255){ // Draw simulation lines
						  if (iS2[i2].value>=990){
							  iS3[i2].target(1000);
						  }
						  else{
							  iS3[i2].set(0);
						  }
							
						  float percent = iS3[i2].value/1000;
						  float xDel = (xR2-xRect)*percent;
						  float yDel = (yR2-yRect2)*percent;
						  parent.stroke(complexRectionColor.getRed(),complexRectionColor.getGreen(),complexRectionColor.getBlue(),newSatForSimulation);
							 parent.line(xRect, yRect2, xRect+xDel, yRect2+yDel);
						  
					  }
					  else{
						  parent.stroke(complexRectionColor.getRed(),complexRectionColor.getGreen(),complexRectionColor.getBlue(),sat);
						 parent.line(xRect, yRect2, xR2, yR2);
					  }	 
				  }
				  if (processedComplexRight.indexOf(id)<0 || sat==255){  // if not drawn yet
					  if (processedComplexRight.indexOf(id)<0)
						  processedComplexRight.add(id);
					
					  for (int k=0;k<components.size();k++){
						  isContainedComplexR =true;
						  if (mapProteinRDFId_index.get(components.get(k))!=null){
							  float y4=iP[mapProteinRDFId_index.get(components.get(k))].value-hProtein/4f;
							  if (check13.s && sat==200)
								  drawGradientLine(xR2, yR2, xR, y4, formComplexColor, sat);
							  else{
								  if (sat==255){ // Draw simulation lines
									  if (iS3[i2].value>=990){
									//	  System.out.println("********1 = "+iS4[i2].value);
										  iS4[i2].target(1000);
									  }
									  else{
										  iS4[i2].set(0);
									  }
										
									  float percent = iS4[i2].value/1000;
									  float xDel = (xR-xR2)*percent;
									  float yDel = (y4-yR2)*percent;
									  parent.stroke(formComplexColor.getRed(), formComplexColor.getGreen(), formComplexColor.getBlue(),newSatForSimulation);
									  parent.line(xR2, yR2, xR2+xDel, yR2+yDel);
									  
								  }
								  else{
									  parent.stroke(formComplexColor.getRed(), formComplexColor.getGreen(), formComplexColor.getBlue(),sat);
									  parent.line(xR2, yR2, xR, y4);
								  }	  
							  }	  
						  }
						  else{
							  if (check12.s && sat==200)
								  drawGradientLine(xR2, yR2, xR, yUFO, unidentifiedElementColor, sat);
							  else{
								  parent.stroke(unidentifiedElementColor.getRed(),unidentifiedElementColor.getGreen(),unidentifiedElementColor.getBlue(),sat);
								  parent.line(xR2, yR2, xR, yUFO);
							  }
						  }
						  
					  }
					  // Draw complex node
					 	
					  parent.noStroke();
					  if (iS3[i2].value>=990)
							parent.fill(complexRectionColor.getRed(),complexRectionColor.getGreen(),complexRectionColor.getBlue(),newSatForSimulation);
					  else
							parent.fill(complexRectionColor.getRed(),complexRectionColor.getGreen(),complexRectionColor.getBlue(),sat);
									
					  parent. pushMatrix();
					  parent.translate(xR2, yR2);
					  polygon(0, 0, rComplexesR[id]/2+1, 4); 
					  parent.popMatrix();
					  
					  if (sat==255 && check5.s){
						  parent.textAlign(PApplet.CENTER);
						  parent.textSize(12);
						  parent.text(main.MainMatrixVersion_1_6.complexList.get(id).getDisplayName(),xR2,yR2-5);
					  }
				  }	  
			  }
			  else if (unidentifiedList.contains(sRight[i3].toString())){
				  float y5 = iY[i2].value-iH[i2].value/2;
				  if (check12.s && sat==200)
					  drawGradientLine(xRect, y5, xR, yUFO, unidentifiedElementColor, sat);
				  else{
					  parent.stroke(unidentifiedElementColor.getRed(),unidentifiedElementColor.getGreen(),unidentifiedElementColor.getBlue(),newSatForSimulation);
					  parent.line(xRect, y5, xR, yUFO);
				  }
				  
			  }
			  else{
				//	 System.out.println("drawReactionLink Right: CAN NOT FIND ="+sRight[i3]);
			  }
			  
		  }
		 	if (!isContainedComplexR && iS3[i2].value>=990){
		 	 iS4[i2].set(1000);
		  }
			  
	 }
	
	void polygon(float x, float y, float radius, int npoints) {
		  float angle = 2*PApplet.PI / npoints;
		  parent.beginShape();
		  for (float a = 0; a <  2*PApplet.PI; a += angle) {
		    float sx = x +  PApplet.cos(a) * radius;
		    float sy = y + PApplet.sin(a) * radius;
		    parent.vertex(sx, sy);
		  }
		  parent.endShape(PApplet.CLOSE);
	}
		
	public void mouseClicked() {
		if (bPopup)
			 sPopup = !sPopup;
		else{
			deleteReactionList = new ArrayList<Integer>();
			if (main.MainMatrixVersion_1_6.buttonDelete.s){
				iDelete.value=0;
				iDelete.target=0;
				deleteProtein = bProteinL;
				for (int r=0;r<rectList.size();r++) {
					BiochemicalReaction rect = rectList.get(r);
					Object[] sLeft = rect.getLeft().toArray();
					for (int i3=0;i3<sLeft.length;i3++){
						 String name = main.MainMatrixVersion_1_6.getProteinName(sLeft[i3].toString());
						  if (name==null)
							  name = sLeft[i3].toString();
						  if (mapProteinRDFId_index.get(name)!=null && mapProteinRDFId_index.get(name)==bProteinL){
							  if (!deleteReactionList.contains(r))
								  deleteReactionList.add(r);
						  }
						  
						  else  if (main.MainMatrixVersion_1_6.mapComplexRDFId_index.get(sLeft[i3].toString())!=null){
							  int id = main.MainMatrixVersion_1_6.mapComplexRDFId_index.get(sLeft[i3].toString());
							  ArrayList<String> components = main.MainMatrixVersion_1_6.proteinsInComplex[id];
							  for (int k=0;k<components.size();k++){
								  if (mapProteinRDFId_index.get(components.get(k))!=null){
									  if(mapProteinRDFId_index.get(components.get(k))==bProteinL){
										  if (!deleteReactionList.contains(r))
											  deleteReactionList.add(r);
									  }	  
								  }		
							  }
								  
						  }
					}	  
				 }
				
				// Retrieve downstream reactions
				//System.out.println(deleteProtein+"	deleteReactionList1="+deleteReactionList);
				ArrayList<Integer> parentList = new ArrayList<Integer>();
				ArrayList<Integer> levelDownStreamList = new ArrayList<Integer>();
				for (int i=0;i<deleteReactionList.size();i++) {
					int r = deleteReactionList.get(i);
					listReactionDownStream(r,0,deleteReactionList,parentList,levelDownStreamList);
				}
			}
			else if (bRect>=0){
				s = bRect;
				simulationRectList = new ArrayList<Integer>();
				simulationRectListLevel= new ArrayList<Integer>();
				interElements =  new ArrayList<String>();
				interElementsLevel =  new ArrayList<Integer>();
				
				simulationRectList.add(s);
				simulationRectListLevel.add(0);
				
				resetIntegrators();
				for (int r=0;r<rectList.size();r++) {
					for (int g=0;g<rectList.size();g++) {
						iS[r][g].set(0);
					}
				}
			}
			else if (bProteinL>=0){
				simulationRectList = new ArrayList<Integer>();
				simulationRectListLevel= new ArrayList<Integer>();
				interElements =  new ArrayList<String>();
				interElementsLevel =  new ArrayList<Integer>();
				for (int r=0;r<rectList.size();r++) {
					BiochemicalReaction rect = rectList.get(r);
					Object[] sLeft = rect.getLeft().toArray();
					for (int i3=0;i3<sLeft.length;i3++){
						 String name = main.MainMatrixVersion_1_6.getProteinName(sLeft[i3].toString());
						  if (name==null)
							  name = sLeft[i3].toString();
						  if (mapProteinRDFId_index.get(name)!=null && mapProteinRDFId_index.get(name)==bProteinL){
							  if (!simulationRectList.contains(r)){
									
							    simulationRectList.add(r);
								simulationRectListLevel.add(0);
							  }	
						  }
					}	  
				 }
				
				resetIntegrators();
				for (int r=0;r<rectList.size();r++) {
					for (int g=0;g<rectList.size();g++) {
						iS[r][g].set(0);
					}
				}
			}
			else {
				simulationRectList = new ArrayList<Integer>();
				simulationRectListLevel = new ArrayList<Integer>();
				interElements =  new ArrayList<String>();
				interElementsLevel =  new ArrayList<Integer>();
				s =-200;
				resetIntegrators();
				for (int r=0;r<rectList.size();r++) {
					for (int g=0;g<rectList.size();g++) {
						iS[r][g].set(0);
					}
				}
			}
			
		}
	}
	 
	public void checkBrushing() {
		if (rectHash==null || iH==null || iH.length==0) return;
		
		int mX = parent.mouseX;
		int mY = parent.mouseY;
		if (xButton<mX && mX<xButton+w1 && 0<=mY && mY<=yBegin){
			bPopup=true;
			return;
		}	
		else if (sPopup){
			bPopup=false;		
			int bRectOld = bRect;
			for (int i=0; i<rectHash.size(); i++){
				if (xRect-50<=mX && mX<=xRect+50 && iY[i].value-iH[i].value<=mY && mY<=iY[i].value){
					if (textbox1.searchText.equals("") || (!textbox1.searchText.equals("") && sRectList.indexOf(i)>=0)) {
						bRect =i;
						if (bRectOld!=bRect){
							resetIntegrators();
						}
						hightlightList[i] = 1; 
						return;
					}
				}	
			}
			
			
		}
		bPopup=false;		
		bRect =-100;
	}
	
}