package code;

import java.io.File;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.FileChooser;
import javafx.util.Duration;

public class MediaPane extends BorderPane {
	private Media media;
	private MediaPlayer mediaPlayer;
	private MediaView mediaView;
	private Button endButton;
	private Button foreButton;
	private Button playButton;
	private Button backButton;
	private Button openButton;
	private Slider slVolume;
	private HBox hBox;
	private Slider slProcess;
	private VBox vBox;
	private FileChooser fileChooser;
	private File mediaFile,prevDirectory;
	private String MEDIA_URL;
	private boolean bCanPlay=false;
	private javafx.stage.Stage theStage;
	
	
	public MediaPane(javafx.stage.Stage stage){
		theStage=stage;
		setPrefSize(800, 600);
		setStyle("-fx-background-color:black");
		fileChooser=new FileChooser();
		fileChooser.setTitle("打开媒体");
		fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("MP4","*.mp4"),
				new FileChooser.ExtensionFilter("MP3", "*.mp3"));
		mediaView=new MediaView();
		
		endButton=new Button("[END]");
		backButton=new Button("<<");
		playButton=new Button(">");
		foreButton=new Button(">>");
		openButton=new Button("[OPEN]");
		
		
		endButton.setOnAction(e->{
			if(bCanPlay){
				mediaPlayer.stop();
				playButton.setText(">");
			}
		});
		
		backButton.setOnAction(e->{
			if(bCanPlay){
				mediaPlayer.seek(mediaPlayer.getCurrentTime().subtract(new Duration(10000)));
			}
		});
		
		playButton.setOnAction(e->dealWithClick());
		
		foreButton.setOnAction(e->{
			if(bCanPlay){
				mediaPlayer.seek(mediaPlayer.getCurrentTime().add(new Duration(10000)));
			}
		});
		
		openButton.setOnAction(e->{
			if(bCanPlay){
				try {
					fileChooser.setInitialDirectory(mediaFile.getParentFile());
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			mediaFile=fileChooser.showOpenDialog(theStage);
			if(mediaFile!=null){	//说明选择了文件
				MEDIA_URL=mediaFile.getAbsolutePath();
				if(MEDIA_URL.endsWith(".mp4")||
						MEDIA_URL.endsWith(".aiff")||MEDIA_URL.endsWith(".mp3")||
						MEDIA_URL.endsWith(".wav")){
					theStage.setTitle(MEDIA_URL);
					MEDIA_URL=MEDIA_URL.replace('\\', '/');
					if(bCanPlay){
						//当前有媒体正在播放，先停止原先正在播放的媒体
						mediaPlayer.stop();
					}
					media=new Media("file:///"+MEDIA_URL);
					mediaPlayer=new MediaPlayer(media);
					mediaPlayer.volumeProperty().bind(slVolume.valueProperty().divide(100));
					mediaPlayer.currentTimeProperty().addListener(new InvalidationListener() {		
						@Override
						public void invalidated(Observable observable) {
							// TODO Auto-generated method stub
								slProcess.setValue(mediaPlayer.getCurrentTime().toMillis()/media.getDuration().toMillis()*2000);
						}	
					});
					mediaView.setMediaPlayer(mediaPlayer);
					bCanPlay=true;
				}
			}
			
			
		});
		
		slVolume=new Slider();
		slVolume.setPrefWidth(150);
		slVolume.setMaxWidth(Region.USE_PREF_SIZE);
		slVolume.setMinWidth(30);
		slVolume.setValue(50);
		
		//控制条
		hBox=new HBox(10);
		hBox.setAlignment(Pos.CENTER);
		hBox.getChildren().addAll(endButton,backButton,playButton,foreButton,openButton,new Label("Volume"),slVolume);
		
		slProcess=new Slider();
		slProcess.setValue(0);
		slProcess.setMax(2000);
		
		//拖动播放条调整媒体播放进度
		slProcess.setOnMouseDragged(e->{
			if(bCanPlay){
				mediaPlayer.seek(new Duration(slProcess.getValue()/2000*media.getDuration().toMillis()));
			}
		});
		vBox=new VBox();
		vBox.getChildren().addAll(slProcess,hBox);
		vBox.setAlignment(Pos.CENTER);
		
		setCenter(mediaView);
		//使MediaView的尺寸和播放器的尺寸关联
		mediaView.fitWidthProperty().bind(widthProperty());
		mediaView.setOnMouseClicked(e->dealWithClick());
		setBottom(vBox);
	}
	
	public void setVideo(String url){
		MEDIA_URL=url;
	}
	
	//点击播放按钮和播放界面是调用该函数
	protected void dealWithClick(){
		if(bCanPlay){
			if(playButton.getText().equals(">")){
				mediaPlayer.play();
				playButton.setText("||");
			}
			else{
				mediaPlayer.pause();
				playButton.setText(">");
			}
		}
	}
	
}
