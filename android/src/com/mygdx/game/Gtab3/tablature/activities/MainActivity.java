package com.mygdx.game.Gtab3.tablature.activities;


import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.mygdx.game.R;
import com.mygdx.game.Gtab3.tablature.generation.TablatureGenerator;
import com.mygdx.game.Gtab3.tablature.interfaces.ITablatureGenerator;
import com.mygdx.game.Gtab3.tablature.tablature.Position;
import com.mygdx.game.Gtab3.tablature.views.TablatureView;

public class MainActivity extends Activity {
	//views in the layout
	private LinearLayout containerLayout;
	//private LinearLayout mainLayout;
	//private LinearLayout borneLayout;
	private HorizontalScrollView scrl;
	private Spinner tabSpin;
	private Spinner minSpin;
	private Spinner maxSpin;
	private Spinner mgSpin;
	private Button bButton;
	private TextView borneTV;
	
	private boolean isInit = false;
	private boolean canGo = false;
	ITablatureGenerator tabGen;
	private TablatureView tabView;
	private String tabName;
	private  String dir;
	private File dirFile;
	private static final String LOG_TAG = "MainActivity: ";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tablature);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

		init();
	}
	
	private void init(){
		dir = "SoundProject"; //"saved";
		dirFile = 
		 new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+dir+"/");//getDir(dir, 0);
		//initialisation des views
		//mainLayout = (LinearLayout) findViewById(R.id.mainLayout);
		//borneLayout = (LinearLayout) findViewById(R.id.borneLayout);
		scrl = (HorizontalScrollView) findViewById(R.id.scrl);
		//containerLayout = (LinearLayout) findViewById(R.id.scrLayout);
		//containerLayout.setBackgroundResource(R.drawable.theme_wood);
		tabSpin = (Spinner) findViewById(R.id.tabSpinner);
		minSpin = (Spinner) findViewById(R.id.minSpinner);
		minSpin.setVisibility(View.INVISIBLE);
		maxSpin = (Spinner) findViewById(R.id.maxSpinner);
		maxSpin.setVisibility(View.INVISIBLE);
		//mgSpin = (Spinner) findViewById(R.id.mgSpinner);
		//mgSpin.setVisibility(View.INVISIBLE);
		bButton = (Button) findViewById(R.id.borneButton);
		//bButton.setVisibility(View.INVISIBLE);
		borneTV = (TextView) findViewById(R.id.bornetv);
		borneTV.setVisibility(View.INVISIBLE);
		setTabSpinItem();
		setTabOSL();
		//setmgOSL();
		setminOSL();
		setmaxOSL();
		setbButtonListener();
		tabView = (TablatureView)findViewById(R.id.custom_view);
        tabView.invalidate();
        tabView.requestLayout();
		//tabView = new TablatureView(this);
		tabGen = new TablatureGenerator(dirFile, tabView);
		
		/*containerLayout.addView((TablatureView)tabView,
	    		new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
	    		LinearLayout.LayoutParams.MATCH_PARENT, (float)1.0));*/
		
	}
	
	private void setTabSpinItem(){
		ArrayList<String> list = getNotesFilesNames();
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, list);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		tabSpin.setAdapter(dataAdapter);
	}
	
	private ArrayList<String> getNotesFilesNames(){
		ArrayList<String> list = new ArrayList<String>();
		list.add("Tablature");
		for(String f: dirFile.list()){
			 if(f.endsWith(".txt")){
				 list.add(f);
			 }
		}
			
		return list;
	}
	
	private void setTabOSL(){
		tabSpin.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int pos, long id) {
				switch (pos) {
				case 0:{
					if(!isInit){
						((TablatureView) tabView).setHeight(scrl.getHeight());
						isInit = true;
						canGo = false;
					}
					//mgSpin.setVisibility(View.INVISIBLE);
					hideBElts();
					break;
				}
				default:{
					if(!isInit){
						((TablatureView) tabView).setHeight(scrl.getHeight());
						isInit = true;
					}
					
					if(!canGo) canGo = true;
					tabName = String.valueOf(parent.getItemAtPosition(pos));
					((TablatureGenerator) tabGen).setNotesName(tabName);
					//new try
					optNBGen();//hideBElts();
					//mgSpin.setVisibility(View.VISIBLE);
                    tabView.invalidate();
					break;
				}
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
			
		});
	}
	
	
	private void setminOSL() {
		minSpin.setOnItemSelectedListener(new OnItemSelectedListener() {
			 @Override
				public void onItemSelected(AdapterView<?> parent, View view,
						int pos, long id) {
				 	int start = Integer.valueOf(
				 			String.valueOf(parent.getItemAtPosition(pos)));	
				 	setmaxSpinItems(start);
				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {
					
				}
		});
	}

	private void setmaxOSL() {

		
	}

	/*private void setmgOSL() {
		mgSpin.setOnItemSelectedListener(new OnItemSelectedListener() {
			 @Override
			 public void onItemSelected(AdapterView<?> parent, View view,
						int pos, long id) {
				 switch (pos) {
				case 1:{//generation aleatoire
					if(canGo) aleaGen();
					hideBElts();
					break;
				}
				case 2:{//generation par optimisation entre deux bornes
					if(canGo) showBElts();
					break;
				}
				case 3:{//generation par optimisation non bornee
					if(canGo) optNBGen();
					hideBElts();
					break;
				}
				default:
					break;
				}
			 }

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
					
			}
		});
	}*/
	
	private void setmaxSpinItems(int start){
		List<String> list = new ArrayList<String>();
		for(int i=start; i<Position.MAXCASE; i++){
			list.add(String.valueOf(i));
		}
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, list);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		maxSpin.setAdapter(dataAdapter);
	}

	private void setbButtonListener() {
		bButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(canGo) borneGen();
			}
		});
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.ihm, menu);
		return true;
	}
	
	public void borneGen(){
		int bmin = Integer.valueOf(String.valueOf(minSpin.getSelectedItem()));
		int bmax = Integer.valueOf(String.valueOf(maxSpin.getSelectedItem()));
		tabGen.optDistBorneConvert(bmin, bmax);
	}
	
	private void optNBGen(){
		tabGen.optDistConvert();
        tabView.invalidate();
	}

	private void aleaGen() {
		tabGen.randomConvert();
	}
	
	private void hideBElts(){
		if(minSpin.getVisibility()==View.VISIBLE || 
				maxSpin.getVisibility()==View.VISIBLE ||
				bButton.getVisibility()==View.VISIBLE ||
				borneTV.getVisibility() ==View.VISIBLE){
			minSpin.setVisibility(View.INVISIBLE);
	 		maxSpin.setVisibility(View.INVISIBLE);
	 		bButton.setVisibility(View.INVISIBLE);
	 		borneTV.setVisibility(View.INVISIBLE);
		}
	}
	
	private void showBElts(){
		minSpin.setVisibility(View.VISIBLE);
 		maxSpin.setVisibility(View.VISIBLE);
 		bButton.setVisibility(View.VISIBLE);
 		borneTV.setVisibility(View.VISIBLE);
	}
	
	public void onReturn(View v){
		this.finish();
	}
	
}
