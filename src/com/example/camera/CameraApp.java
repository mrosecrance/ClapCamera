package com.example.camera;

import java.io.File;
import java.io.FileOutputStream;
import android.widget.Button;
import java.io.IOException;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ShareActionProvider;
import android.widget.Toast;

public class CameraApp extends Activity implements SurfaceHolder.Callback {
	private static final String TAG = "PhoneController "; 
	private static final String IMAGE_FOLDER = "/PhoneController/";
	private static final String EXTENTION = ".jpg";
	private String pictureName = "";
	public String picNameToshare = "";
	static final int FOTO_MODE = 0;
	String speed;
	String imageFilePath;
	private Handler mHandler = new Handler();
	public static String imageFilePath1;
	FileOutputStream fos = null;
	public Bitmap framebmpScaled;
	public Bitmap SelecedFrmae;
	public static Bitmap mergeBitmap;
	public static Bitmap bmpfULL;
	Camera camera;
	Button button;
	SurfaceView surfaceView;
	SurfaceHolder surfaceHolder;
	boolean previewing = false;
	LayoutInflater controlInflater = null;
	private ShareActionProvider mShareActionProvider;
	Button share;

	  /** Called when the activity is first created. */
	  @Override
	  public void onCreate(Bundle savedInstanceState) {
	        
	      super.onCreate(savedInstanceState);
	     requestWindowFeature(Window.FEATURE_NO_TITLE);
	    
	     setContentView(R.layout.activity_camera_app);
	     setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	
	     getWindow().setFormat(PixelFormat.UNKNOWN);
	      surfaceView = (SurfaceView) findViewById(R.id.camerapreview);
	     surfaceHolder = surfaceView.getHolder();
	
	      surfaceHolder.addCallback(this);
	
	      surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	      button = (Button)findViewById(R.id.button);
	      button.setOnClickListener(buttonListener);
	      
	      share = (Button)findViewById(R.id.share);
	      share.setOnClickListener(shareListener);
	     
	     
	  }
	  private OnClickListener buttonListener = new OnClickListener() {
	         public void onClick(View v) {
	                Handler myHandler = new Handler();
	             myHandler.postDelayed(mMyRunnable, 5000); // called after 5 seconds
	             button.setText("Waiting...");
	         }};
     private OnClickListener shareListener = new OnClickListener() {
         public void onClick(View v) {

       	  Intent shareIntent = new Intent();
       	  shareIntent.setAction(Intent.ACTION_SEND);
       	  shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://"+ Environment.getExternalStorageDirectory()));
       	  shareIntent.setType("image/jpeg");
       	  startActivity(Intent.createChooser(shareIntent, "Share image using"));
         }};
		 	 
	         
	 
	  private Runnable mMyRunnable = new Runnable()
	  {
	
	    public void run() {
	        Camera.Parameters params = camera.getParameters();
	         params.set("rotation", 90);
	         camera.setParameters(params);
	    camera.takePicture(myShutterCallback,
	      myPictureCallback_RAW, myPictureCallback_JPG);
	         storePicture(mergeBitmap);
	         button.setText("Capture another Image");  
	
	}
	  };
	
	  ShutterCallback myShutterCallback = new ShutterCallback(){
	 public void onShutter() {
	  // TODO Auto-generated method stub
	
	 }};
	
	PictureCallback myPictureCallback_RAW = new PictureCallback(){
	
	
	 public void onPictureTaken(byte[] arg0, Camera arg1) {
	  // TODO Auto-generated method stub
	
	 }};
	
	PictureCallback myPictureCallback_JPG = new PictureCallback(){
	
	
	 public void onPictureTaken(byte[] arg0, Camera arg1) {
	  // TODO Auto-generated method stub
	        
	        Display display = getWindowManager().getDefaultDisplay();
	              final int ScreenWidth = display.getWidth();
	              final int ScreenHeight = display.getHeight();
	        Bitmap bitmapPicture = BitmapFactory.decodeByteArray(arg0, 0, arg0.length);
	        Bitmap bmpScaled1 = Bitmap.createScaledBitmap(bitmapPicture, ScreenWidth, ScreenHeight,
	              true);
	        
	      
	        mergeBitmap=bmpScaled1;
	
	      
	        showPicture();     
	        
	      
	 }};
	
	
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
	  int height) {
	 // TODO Auto-generated method stub
	 if(previewing){
	  camera.stopPreview();
	  previewing = false;
	 }
	
	 if (camera != null){
	  try {
	   camera.setPreviewDisplay(surfaceHolder);
	   camera.startPreview();
	   previewing = true;
	  } catch (IOException e) {
	   // TODO Auto-generated catch block
	   e.printStackTrace();
	  }
	 }
	}
	
	
	public void surfaceCreated(SurfaceHolder holder) {
	 // TODO Auto-generated method stub
	       camera = Camera.open();
	       camera.setDisplayOrientation(90);
	}
	
	public void surfaceDestroyed(SurfaceHolder holder) {
	 // TODO Auto-generated method stub
	
	 camera.stopPreview();
	 camera.release();
	 camera = null;
	 previewing = false;
	}
	
	void storePicture(Bitmap bm){
	       mHandler.post(new Runnable(){
	              public void run(){
	              //     showPicture();                                 
	              }                         
	       });
	       this.pictureName = TAG + String.valueOf((System.currentTimeMillis())%10000);
	       picNameToshare = this.pictureName;
	       this.imageFilePath =  IMAGE_FOLDER + this.pictureName + EXTENTION;
	       this.imageFilePath = sanitizePath(this.imageFilePath);
	      
	       try{
	              checkSDCard(this.imageFilePath);
	              fos = new FileOutputStream(this.imageFilePath);
	             
	              if(fos != null){                 
	             
	                     bm.compress(Bitmap.CompressFormat.JPEG, 85, fos);
	              Toast.makeText(this, "Image Saved", Toast.LENGTH_SHORT).show();
	
	                     fos.close();                            
	              }
	       }catch(IOException ioe){
	             
	              Log.e(TAG, "CapturePicture : " + ioe.toString());
	       }catch(Exception e){
	             
	              Log.e(TAG, "CapturePicture : " + e.toString());
	       }
	       
	  sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED,
	  Uri.parse("file://"+ Environment.getExternalStorageDirectory())));
	  imageFilePath1= this.imageFilePath;
	  
	  share.setVisibility(0);
	  
	  
	}
	
	/**
	 * Check the SDCard is mounted on device
	 * @param path of image file
	 * @throws IOException
	 */
	void checkSDCard(String path) throws IOException {
	       String state = android.os.Environment.getExternalStorageState();
	    if(!state.equals(android.os.Environment.MEDIA_MOUNTED))  {
	       Toast.makeText(this, "Please insert sdcard other wise image won't stored", Toast.LENGTH_SHORT).show();
	        throw new IOException("SD Card is not mounted.  It is " + state + ".");
	    }
	    File directory = new File(path).getParentFile();
	    if (!directory.exists() && !directory.mkdirs()) {
	      throw new IOException("Path to file could not be created.");
	    }
	}
	
	private String sanitizePath(String path) {
	    if (!path.startsWith("/")) {
	      path = "/" + path;
	    }
	   
	    return Environment.getExternalStorageDirectory().getAbsolutePath() + path;
	}
	
	void showPicture(){
	           
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    // Inflate menu resource file.
	    //getMenuInflater().inflate(R.menu.share_menu, menu);

	    // Locate MenuItem with ShareActionProvider
	    MenuItem item = menu.findItem(R.id.menu_item_share);

	    // Fetch and store ShareActionProvider
	    mShareActionProvider = (ShareActionProvider) item.getActionProvider();

	    // Return true to display menu
	    return true;
	}

	// Call to update the share intent
	private void setShareIntent(Intent shareIntent) {
	    if (mShareActionProvider != null) {
	        mShareActionProvider.setShareIntent(shareIntent);
	    }
	}
	
}