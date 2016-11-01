package com.example.giovanazzi.trackdroid_bt;


import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

public class ll_inicio_aplicacion extends Activity {
	
	public static final String DEVICE_EXTRA = "com.blueserial.SOCKET";
	public static final String DEVICE_UUID = "com.blueserial.uuid";
	public static final String BUFFER_SIZE = "com.blueserial.buffersize";
	
	public static final String TAG = "ISITE PROYECTO";
	public int mMaxChars   = 50000;//Default

	public BluetoothSocket mBTSocket;
	public ReadInput mReadThread = null;

	public boolean mIsUserInitiatedDisconnect = false;
	public Boolean Apuntamiento=false,Booteo=true,Habilitacion=false;

	public UUID mDeviceUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); // Standard SPP UUID
	
	public int mBufferSize = 50000; //Default

	public Float NivelGlobal;
	public int NivelGlobalInt=0;
	public String strInputGlobal="";
	public boolean mIsBluetoothConnected = false;

	public BluetoothDevice mDevice;

	//////////////////////////////////////////////////////////////////
	// dialogos en progreso
	public ProgressBar progressBarBoot;
	public ProgressDialog progressDialog,dialogoCargaConfiguracion,progressDialogOPT;


	public Button btn_Prueba,btn_RestaurarOPT,btn_Led,btn_EnviarOPT,btn_exit,btn_Ingresar,btn_Cargar_OPT,btn_SetFreq,btn_Reset,btn_Browser,btn_SetPower;
	public ToggleButton TB_Login,TB_CwOnOff,TB_Pointing;
	public TextView  Text_lineas,Text_Log,Text_Path,TextPointing,TextPrueba,TextNivel,Text_Serial,Text_Modelo,Text_Firmware,Text_VersionLinux;

	public String password;
	// hilos
	
	public Handler puente;
	//public VentanaDialogoNivel DialogoNivel;
	public Boolean Lectura_pointing=false,boolPassword=true, telnet=true;
	
	public Thread th1,thOpt;
	//private asincOPT asinc;
	//private MedirNivelSenal asincMedirNivel;
	////// opt 
	static char finCadena=0x03;
	
	private static final int REQUEST_PATH = 1;
	 
	String curFileName,curFilePath;
	
	EditText EditPath;
	

	 static String p,StringRecepcion;
	 String[] CadenaPartida;
	 int longitudArchivo;



	ToggleButton tb_relay1,tb_relay2,tb_relay3,tb_relay4,tb_relay5,tb_relay6;
	Button btn_LeerConfigRemota,btn_GrabarConfiguracion,btn_Display,btn_prueba;
	TextView txt_RxDatos;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ll_inicio_aplicacion);
		ActivityHelper.initialize(this);
		Intent intent = getIntent();
		Bundle b = intent.getExtras();
		mDevice = b.getParcelable(Homescreen.DEVICE_EXTRA);
		mDeviceUUID = UUID.fromString(b.getString(Homescreen.DEVICE_UUID));
		mMaxChars = b.getInt(Homescreen.BUFFER_SIZE);
		
		LevantarXML();
		Botones();


		}
	
	/////////////// Dialogos /////////////////////////////////

	private void DialogoCargaConfiguracion(){

		dialogoCargaConfiguracion = new ProgressDialog(ll_inicio_aplicacion.this);
		dialogoCargaConfiguracion.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		dialogoCargaConfiguracion.setMessage("Cargando configuracion REMOTA...");
		dialogoCargaConfiguracion.setCancelable(true);
		dialogoCargaConfiguracion.show();

	}


////////////////// Fin Dialogos ////////////////////////////////


	private void  LevantarXML() {

		tb_relay1=(ToggleButton) findViewById(R.id.tb_relay1);
		tb_relay2=(ToggleButton) findViewById(R.id.tb_relay2);
		tb_relay3=(ToggleButton) findViewById(R.id.tb_relay3);
		tb_relay4=(ToggleButton) findViewById(R.id.tb_relay4);
		tb_relay5=(ToggleButton) findViewById(R.id.tb_relay5);
		tb_relay6=(ToggleButton) findViewById(R.id.tb_relay6);

		btn_LeerConfigRemota=(Button)findViewById(R.id.btn_LoadConfRem);
		btn_GrabarConfiguracion=(Button)findViewById(R.id.btn_GrabarConfiguracion);
		btn_Display=(Button)findViewById(R.id.btn_Display);
		btn_prueba=(Button)findViewById(R.id.btn_prueba);

		txt_RxDatos=(TextView)findViewById(R.id.txt_rxDatos);
	}

	private void Botones() {

		btn_prueba.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intento= new Intent(getApplicationContext(),Activity_Relays.class);
				startActivity(intento);

			}
		});

		btn_Display.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				FuncionEnviar("777");

			}
		});

		btn_LeerConfigRemota.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				FuncionEnviar("000");
				DialogoCargaConfiguracion();

			}
		});

		btn_GrabarConfiguracion.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				FuncionEnviar("999");

			}
		});

		tb_relay1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					FuncionEnviar("101");
				}else{
					FuncionEnviar("100");
				}
			}
		});

		tb_relay2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					FuncionEnviar("201");
				}else{
					FuncionEnviar("200");
				}
			}
		});
		tb_relay3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					FuncionEnviar("301");
				}else{
					FuncionEnviar("300");
				}
			}
		});
		tb_relay4.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					FuncionEnviar("401");
					}else{
					FuncionEnviar("400");
				}
			}
		});
		tb_relay5.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					FuncionEnviar("501");
				}else{
					FuncionEnviar("500");
				}
			}
		});
		tb_relay6.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					FuncionEnviar("601");
				}else{
					FuncionEnviar("600");
				}
			}
		});
	}

	////////////// FUNCION PARA ENVIAR ///////////////////
	
	public void FuncionEnviar(String StringEnviado){
		
		try {
			mBTSocket.getOutputStream().write((StringEnviado+"\r").getBytes());
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

	///////// FUNCION PARA DETECTAR LOS STRIUNGS D EENTRADA////////////////

	public void FuncionDetectarComando(String detectorString,Boolean hab){

		// separador de CadenaPArtida para tenerla en lineas.
		String[] CadenaPartida = detectorString.split("\r");
		int longitud =CadenaPartida.length;
		StringRecepcion=detectorString;
		runOnUiThread(new Runnable() {
			public void run() {
				txt_RxDatos.setText(StringRecepcion);
				Toast.makeText(getApplicationContext(), StringRecepcion, Toast.LENGTH_SHORT).show();
			}
		});

		if(detectorString.contains("000")){
			 runOnUiThread(new Runnable() {
       public void run() {dialogoCargaConfiguracion.dismiss(); }
			    });
		}

		if(detectorString.contains("[ErrorStack]")){
			FuncionEnviar("exit");
			runOnUiThread(new Runnable() {
        public void run() {Toast.makeText(getApplicationContext(), "Necesita el OPT !!!", Toast.LENGTH_SHORT).show();}
			    });
			}

		if(hab){
			if(detectorString.contains("SN: ")){
			 runOnUiThread(new Runnable() {
				 	int posicion =strInputGlobal.indexOf("SN:");
			        public void run() {
			        	   Text_Serial.setText(strInputGlobal.substring(posicion+3,posicion+15));
			        	     }
			    });
			}
			if(detectorString.contains("Model: ")){
			 runOnUiThread(new Runnable() {
			        int posicion =strInputGlobal.indexOf("Model:");
			        public void run() {
			        	   Text_Modelo.setText(strInputGlobal.substring(posicion+6,posicion+15));
		        	   }
			    });
			}



		if(detectorString.contains("iDirect Linux-BSP Release")){
			 runOnUiThread(new Runnable() {
				 int posicion =strInputGlobal.indexOf("iDirect Linux-BSP Release");
			        public void run() {
			        	   Text_VersionLinux.setText(strInputGlobal.substring(posicion+25,posicion+35));
		        	   }
			    });
			}

		//Code Version:
		if(detectorString.contains("Code Version:")){
			 runOnUiThread(new Runnable() {
				 	int posicion =strInputGlobal.indexOf("Code Version:");
			        public void run() {
			        	   Text_Firmware.setText(strInputGlobal.substring(posicion+13,posicion+23));
		        	   }
			    });
			}

			////////// Usuario Admin
		if (detectorString.contains("Username:")){
				FuncionEnviar("admin");
			}
			/////////Password
		if(detectorString.contains("Password:")){
			final String pass=password;//EditPass.getText().toString();
			runOnUiThread(new Runnable() {
				        public void run() {
							        	Log.d("FuncionDetectarComando","PASS EDIT: "+pass);
							        	Toast.makeText(getApplicationContext(), "Password: "+pass, Toast.LENGTH_SHORT).show();
										  }
							    });
				 FuncionEnviar(pass);
				 FuncionEnviar("versions_report");
						}


		if(detectorString.contains(">")&telnet){
				telnet=false;
			 runOnUiThread(new Runnable() {
				public void run() {    	btn_Browser.setEnabled(true);
							    		btn_EnviarOPT.setEnabled(false);
							        	btn_exit.setEnabled(true);
										btn_Ingresar.setEnabled(false);
										btn_SetFreq.setEnabled(true);
										btn_SetPower.setEnabled(true);
										TB_Pointing.setEnabled(true);
										TB_CwOnOff.setEnabled(true);
										//Toast.makeText(getApplicationContext(), " Logueado en Telnet", Toast.LENGTH_SHORT).show();
										//progressDialogLinux.dismiss();
									       }
							    });
				}

         if(detectorString.contains("Access Denied")){

				telnet=false;
				 runOnUiThread(new Runnable() {
							        public void run() {
										  Toast.makeText(getApplicationContext(), " Error de Password", Toast.LENGTH_SHORT).show();
										  Habilitacion=false;

									       }
							    });
				}
		if(detectorString.contains(("tx cw on"))||detectorString.contains("tx cw off")){
				FuncionEnviar("tx cw");
				}

		if(detectorString.contains("cw =")){
				 	  runOnUiThread(new Runnable() {
		        int posicion =strInputGlobal.indexOf("=");
					        public void run() {
								  Toast.makeText(getApplicationContext(), " Clean Carrier = "+strInputGlobal.substring(posicion+2,posicion+5), Toast.LENGTH_SHORT).show();
							       }
					    });
				}
		if(detectorString.contains("pointing =")){
				 runOnUiThread(new Runnable() {
		        int posicion =strInputGlobal.indexOf("=");
					        public void run() {
					        	 Toast.makeText(getApplicationContext(), "Point = "+strInputGlobal.substring(posicion+2,posicion+5), Toast.LENGTH_SHORT).show();
							        }
					    });
			}
		if(detectorString.contains("power =")||detectorString.contains("Tx Power   =")){
				 runOnUiThread(new Runnable() {
					 int posicion =strInputGlobal.indexOf("=");
					        public void run() {
					        	 Toast.makeText(getApplicationContext(), "Tx Power = "+strInputGlobal.substring(posicion+2,posicion+5)+" dbm", Toast.LENGTH_SHORT).show();
					        }
					    });
			}
		if(detectorString.contains("Tx Frequency")){
				 runOnUiThread(new Runnable() {

				        int posicion =strInputGlobal.indexOf("=");

				        public void run() {
				        	   Toast.makeText(getApplicationContext(), "Freq = "+strInputGlobal.substring(posicion+2,posicion+15), Toast.LENGTH_LONG).show();
							       }
				    });
				}
			}
	//////// *****   LINUX *****//////////////////////
	else
			{
				telnet =true;
				if(detectorString.contains("# ")){
					 runOnUiThread(new Runnable() {
						 public void run() {Text_Log.setText("Log Linux");

						 					btn_Ingresar.setEnabled(true); }
					    							});
												}
				if(detectorString.contains("rm: remove")){
					FuncionEnviar("y");}

				if(detectorString.contains("falcon_monitor:OK")){
					 runOnUiThread(new Runnable() {
					        public void run() {

					        	Toast.makeText(getApplicationContext(), "Archivo OPT Inicializado !!!", Toast.LENGTH_LONG).show();
					        	      	  }
					    });
					}

				if(detectorString.contains("DRAM Test")){
					 runOnUiThread(new Runnable() {

					        public void run() {
					        	Toast.makeText(getApplicationContext(), "Reiniciando equipo", Toast.LENGTH_LONG).show();
					        	btn_Ingresar.setEnabled(false);
					        	  }
					    });
					}
				if (detectorString.contains("iDirect login:")){
					FuncionEnviar("root");}

			if(detectorString.contains("Password:")){
			Log.d("Linux", "Password Linux:");

			if(boolPassword){
				FuncionEnviar("P@55w0rd!");
				Log.d("FuncionDetectarComando","FuncionEnviar(P@55w0rd!);");
				 runOnUiThread(new Runnable() {

				        public void run() {
				       	btn_Ingresar.setEnabled(true);
				        	  }
				    });

				}
				else{
					FuncionEnviar("iDirect");
					runOnUiThread(new Runnable() {
						public void run() {
				       	btn_Ingresar.setEnabled(true);}
				    });
				Log.d("FuncionDetectarComando","FuncionEnviar(iDirect);");
					}
				}
			if(detectorString.contains("Login incorrect")){
				if (boolPassword){
					boolPassword=false;
				}else{
					boolPassword=true;
				}
			}
		}

		  //strInputGlobal="";
	}

	////////////***   Bluetooth    INICIO ******///////////////////////////////
	
	public class DisConnectBT extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
		}

		@Override
		protected Void doInBackground(Void... params) {

			if (mReadThread != null) {
				mReadThread.stop();
				while (mReadThread.isRunning())
					; // Wait until it stops
				mReadThread = null;

			}

			try {
				mBTSocket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			mIsBluetoothConnected = false;
			if (mIsUserInitiatedDisconnect) {
				finish();
			}
		}

	}
	
	public void msg(String s) {
		Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
	}

	@Override
	protected void onPause() {
		if (mBTSocket != null && mIsBluetoothConnected) {
			new DisConnectBT().execute();
		}
		Log.d(TAG, "Paused");
		super.onPause();
	}

	@Override
	protected void onResume() {
		if (mBTSocket == null || !mIsBluetoothConnected) {
			new ConnectBT().execute();
		}
		Log.d(TAG, "Resumed");
		super.onResume();
	}

	@Override
	protected void onStop() {
		if (mBTSocket != null && mIsBluetoothConnected) {
			new DisConnectBT().execute();
		}
		Log.d(TAG, "Stopped");
		super.onStop();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
	}

	public class ConnectBT extends AsyncTask<Void, Void, Void> {
		private boolean mConnectSuccessful = true;

		@Override 
		protected void onPreExecute() {
			progressDialog = ProgressDialog.show(ll_inicio_aplicacion.this, "Modulo Bluetooth...", "Conectando");// http://stackoverflow.com/a/11130220/1287554

			
		}

		@Override
		protected Void doInBackground(Void... devices) {
			try {
				if (mBTSocket == null || !mIsBluetoothConnected) {
					mBTSocket = mDevice.createInsecureRfcommSocketToServiceRecord(mDeviceUUID);
					BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
					mBTSocket.connect();
				}
			} catch (IOException e) {
				// Unable to connect to device
				e.printStackTrace();
				mConnectSuccessful = false;
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);

			if (!mConnectSuccessful) {
				Toast.makeText(getApplicationContext(), "No de puede conectar. Es un dispositivo serial? Chequear el UUID si esta seteado correctamente", Toast.LENGTH_LONG).show();
				finish();
			} else {
				msg("Connected to device");
				mIsBluetoothConnected = true;
				mReadThread = new ReadInput(); // Kick off input reader	
			}
			progressDialog.dismiss();
			FuncionEnviar("exit");
			
			
		//	FuncionEnviar("\r");
			
		}
	}

	public class ReadInput implements Runnable {

		private boolean bStop = false;
		private Thread t;

		public ReadInput() {
			t = new Thread(this, "Input Thread");
			t.start();
		}

		public boolean isRunning() {
			return t.isAlive();
		}

		@Override
		public void run() {
			InputStream inputStream;

			try {
				inputStream = mBTSocket.getInputStream();
				while (!bStop) {
					byte[] buffer = new byte[256];
					if (inputStream.available() > 0) {
						inputStream.read(buffer);
						int i = 0;
						for (i = 0; i < buffer.length && buffer[i] != 0; i++) {
						}
						final String strInput = new String(buffer, 0, i);
						strInputGlobal=strInput;




						FuncionDetectarComando(strInputGlobal,Habilitacion);
						Log.d("entrada de dato", strInput);
						}
					Thread.sleep(500);
				}
			} catch (IOException e) {
				
				e.printStackTrace();
			} catch (InterruptedException e) {
				
				e.printStackTrace();
			}
	}

		public void stop() {
			bStop = true;
		}

	}

	////////////***   Bluetooth    FIN ******///////////////////////////////

/*
    public class VentanaDialogoNivel extends AsyncTask<Void, Void, Void> {

	@Override
		protected void onPreExecute() {
		}

		@Override
		protected Void doInBackground(Void... devices) {
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);

			String[] NivelesAlmacenados = strInputGlobal.split("\r");

			try {
				float nivelFlotante= Float.parseFloat(NivelesAlmacenados[0])*100;
				int NivelBaliza=(int)nivelFlotante;

				TextNivel.setText("Nivel= -"+NivelBaliza+" dbm");

			} catch (Exception e) {

				TextNivel.setText("Error en la medicion");
			}

			strInputGlobal="";
		}

	}


    public class MedirNivelSenal extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {

			  Toast.makeText(getApplicationContext(), "Comienza la medici�n", Toast.LENGTH_SHORT).show();

		}

		@Override
		protected Void doInBackground(Void... params) {

			while(!isCancelled()){
			try {
				Thread.sleep(1500);
				FuncionEnviar("rx power");

			} catch (Exception e) {
				// TODO: handle exception
			}



			}
			return null;

		}

		protected void onProgressUpdate(String progreso) {

		}
		@Override
		protected void onPostExecute(Void result) {
		//	TextNivel.setText("0 dbm");
			}

		@Override
		protected void onCancelled(Void result) {

			  Toast.makeText(getApplicationContext(), "Medicion Cancelada", Toast.LENGTH_SHORT).show();


		}
	}

*/
public void Hilo() {
		Log.d("Hilo", "th1 = new Thread(new Runnable()");
		th1 = new Thread(new Runnable() {


            @Override
            public void run() {
            	 runOnUiThread(new Runnable() {
            	        public void run() {
            	    	progressDialog = new ProgressDialog(ll_inicio_aplicacion.this);
            	    		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            	    		progressDialog.setMessage("Esperando medicion del Equipo...");
            	    		progressDialog.setMax(10);
            	    		progressDialog.setProgress(0);
            	    		progressDialog.setCancelable(false);
            	    		progressDialog.show();
            	        }
            	    });


            	try {
            		Log.d("Hilo", "th1 = Thread.sleep(11000)");

            		Thread.sleep(12000);
            		progressDialog.cancel();
				} catch (Exception e) {
					// TODO: handle exception
				}



            	while(Lectura_pointing){
            		Log.d("Hilo", "while");
           try {
            	Thread.sleep(1000);
				Log.d("Hilo", "DialogoNivel.execute()");
				//DialogoNivel= new VentanaDialogoNivel();
				//DialogoNivel.execute();

			} catch (InterruptedException e) {
				Lectura_pointing=false;
				e.printStackTrace();
			}
            	}
            	FuncionEnviar("tx iflDC on");

            }
          });
		}

	//////////////////////// cargar opt
	// Listen for results.
/*
	private class asincOPT extends AsyncTask<Void, Void, Void>{

		@Override
        protected void onPreExecute() {
			Toast.makeText(getApplicationContext(), "comienza hilo", Toast.LENGTH_SHORT);
			Text_Path.setText("El OPT fue seleccionado.");
        	FuncionEnviar("cd /etc/idirect/falcon");

        	FuncionEnviar("cat>falcon.opt");
			progressDialogOPT = new ProgressDialog(ll_inicio_aplicacion.this);
			progressDialogOPT.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			progressDialogOPT.setMessage("Cargando OPT");
			progressDialogOPT.setMax(longitudArchivo);
			progressDialogOPT.setProgress(0);
			progressDialogOPT.setCancelable(false);
			progressDialogOPT.show();
			Text_lineas.setText("El archvo tiene "+longitudArchivo+" lineas a Transmitir");

            }

		@Override
		protected Void doInBackground(Void... params) {
			for(int i=0;i<longitudArchivo;i++){
				FuncionEnviar(CadenaPartida[i]);
				try {
					Thread.sleep(5);
					onProgressUpdate(i);

				} catch (Exception e) {
					// TODO: handle exception
				}
			}

			 FuncionEnviar(""+finCadena);
 				 FuncionEnviar("service idirect_falcon restart");

			return null;
		}

		protected void onProgressUpdate(int progreso) {
	              progressDialogOPT.setProgress(progreso);
	        }

		@Override
		protected void onPostExecute(Void unVoid){
			Log.d("ASINC", "onPostExecute ");
			progressDialogOPT.dismiss();
			btn_RestaurarOPT.setEnabled(true);
			btn_EnviarOPT.setEnabled(false);


		}

	}

	public void HiloOPT() {
		Log.d("HiloOPT", "hilo opete");
		thOpt = new Thread(new Runnable() {


          @Override
            public void run() {
            	 runOnUiThread(new Runnable() {
            	        public void run() {

            	        	Text_Path.setText("El OPT fue seleccionado.");
            	        	 FuncionEnviar("cd /etc/idirect_falcon");
            	        	 FuncionEnviar("cat>falcon.opt");
            	        	progressDialogOPT = new ProgressDialog(ll_inicio_aplicacion.this);
            				progressDialogOPT.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            				progressDialogOPT.setMessage("Cargando OPT");
            				progressDialogOPT.setMax(longitudArchivo);
            				progressDialogOPT.setProgress(0);
            				progressDialogOPT.setCancelable(false);
            				progressDialogOPT.show();
            				Text_lineas.setText("El archvo tiene "+longitudArchivo+" lineas a Transmitir");
                			for(int i=0;i<longitudArchivo;i++){
                				FuncionEnviar(CadenaPartida[i]);

                				try {
									Thread.sleep(20);
								//	progressDialogOPT.incrementProgressBy(i);
								} catch (Exception e) {
									// TODO: handle exception
								}
              			}
                			 FuncionEnviar(""+finCadena);
                			 FuncionEnviar("service idirect_falcon restart");
            	        }
            	    });
            }
          });
		}
*/
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        // See which child activity is calling us back.
    	if (requestCode == REQUEST_PATH){
    		if (resultCode == RESULT_OK) {
    			curFileName = data.getStringExtra("GetFileName");
    			curFilePath = data.getStringExtra("GetPath");
    			Text_Path.setText(curFilePath+"/"+curFileName);

    			/////////// carga opt
    			Log.d("OPT", "boton opt");

				p=LeerArchivo(Text_Path.getText().toString());

				CadenaPartida = p.split("\n");
				longitudArchivo=CadenaPartida.length;

				Log.d("OPT", "lineas= "+longitudArchivo);

				for(int i=0;i<longitudArchivo;i++){
					Log.d("OPT cargado: ",CadenaPartida[i]);
				}
				//FuncionEnviar("exit");// para ir a linux
				Habilitacion=false;
				btn_EnviarOPT.setEnabled(true);

    		}
    	 }
    }

    public static String LeerArchivo(String nombre)
    //El parametro nombre indica el nombre del archivo por ejemplo "prueba.txt"
	{

	try{
		File f;
		FileReader lectorArchivo;
		//Creamos el objeto del archivo que vamos a leer
		f = new File(nombre);
		//Creamos el objeto FileReader que abrira el flujo(Stream) de datos para realizar la lectura
		lectorArchivo = new FileReader(f);

		//Creamos un lector en buffer para recopilar datos a travez del flujo "lectorArchivo" que hemos creado
		BufferedReader br = new BufferedReader(lectorArchivo);

		String l="";
		//Esta variable "l" la utilizamos para guardar mas adelante toda la lectura del archivo

		String aux="";/*variable auxiliar*/

		while(true)
			//este ciclo while se usa para repetir el proceso de lectura, ya que se lee solo 1 linea de texto a la vez
		{
			aux=br.readLine();
			//leemos una linea de texto y la guardamos en la variable auxiliar
			if(aux!=null)
				l=l+aux+"\n";
			/*si la variable aux tiene datos se va acumulando en la variable l,
			 * en caso de ser nula quiere decir que ya nos hemos leido todo
			 * el archivo de texto*/
			else
				break;
		}
		br.close();
		lectorArchivo.close();
		return l;
		}catch(IOException e){
		System.out.println("Error:"+e.getMessage());
		}
		return null;
		}
	
 	}
	
	
	

