import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.scene.media.MediaPlayer.Status;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.event.ActionEvent;
import javafx.util.Duration;

//Controller class is the central class where all other classes are linked and mediaplayer base is formed here
public class Controller implements Initializable {

    @FXML
    private MediaView mediaView;

    @FXML
    private Button stopButton, fileChooseButton, Playbutton, playlistButton, bwd, fwd, closePlaylistButton,
            addToPlaylistButton, PlayTheList, nextBtn, prevBtn;

    private Stage stage;

    private File file;
    private Media media;
    private MediaPlayer mediaPlayer;
    private String filePath;
    private FileChooser fileChooser;
    private int isPlaylist;

    @FXML
    Slider timeSlider = new Slider();

    @FXML
    Slider volSlider = new Slider();

    @FXML
    private ComboBox<String> speedControl;

    @FXML
    private ImageView volIMG, pausedIMG;
    @FXML
    private BorderPane playlistPane;
    @FXML
    private ScrollPane playlistView;
    @FXML
    private VBox PlaylistBox;
    @FXML
    private HBox playlistItem, musicIMGpane, mediapane;
    Image playIMG = new Image("file:res/play.png");
    Image pauseIMG = new Image("file:res/pause.png");

    @FXML
    private Label playTime, MediaTitle;
    String title;

    MyTimeSlider mytimeSlider;
    MyVolSlider myVolSlider;
    MyPlayPause myPlayPause;
    MySpeedChange mySpeedChange;
    MyPlaylist mp;

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        stage = new Stage();

        // filePath = "C:\\Users\\mahes\\Downloads\\parody.mp3";
        filePath = ".\\res\\Ishq Jaisa Kuch Fighter 320 Kbps.mp3";

        file = new File(filePath);
        media = new Media(file.toURI().toString());
        mediaPlayer = new MediaPlayer(media);
        mediaView.setMediaPlayer(mediaPlayer);
        addToPlaylistButton = new Button();
        nextBtn.setVisible(false);
        prevBtn.setVisible(false);
        isPlaylist = 0;

        setPlayer();

        timeSlider.valueProperty().addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable ob) {
                if (timeSlider.getValue() == 100 && isPlaylist == 1) {
                    next();
                }
            }
        });
        mp = new MyPlaylist(addToPlaylistButton, PlayTheList, PlaylistBox, mediaPlayer, stage);
        playlistPane.setVisible(false);

    }

    public void setPlayer() {
        media = new Media(file.toURI().toString());
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.seek(mediaPlayer.getStartTime());
        volSlider.setValue(100);
        timeSlider.setValue(0);
        mediaPlayer.setOnReady(() -> stage.sizeToScene());
        mediaView.setMediaPlayer(mediaPlayer);
        mediaPlayer.setAutoPlay(true);

        String fileName = file.getName();
        String extension = "";
        int i = fileName.lastIndexOf('.');
        extension = fileName.substring(i + 1);
        title = fileName.substring(0, i);
        MediaTitle.setText(title);
        MediaTitle.setVisible(false);
        if (extension.equals("mp4")) {
            musicIMGpane.setVisible(false);
            mediapane.setVisible(true);
        } else {
            musicIMGpane.setVisible(true);
            mediapane.setVisible(false);
        }
        mytimeSlider = new MyTimeSlider(mediaPlayer, playTime, timeSlider);
        myVolSlider = new MyVolSlider(volSlider, volIMG, mediaPlayer);
        myPlayPause = new MyPlayPause(mediaPlayer, pausedIMG, Playbutton);
        mySpeedChange = new MySpeedChange(speedControl, mediaPlayer);
    }

    public void handle() {
        myPlayPause.OnButton();
    }

    public void openPlaylist() {
        if (playlistPane.isVisible() == false) {
            playlistPane.setVisible(true);
        } else
            playlistPane.setVisible(false);
    }

    public void closePlaylistPane() {
        playlistPane.setVisible(false);
    }

    public void stopMedia() {
        mediaPlayer.stop();
        mediaPlayer.pause();
        pausedIMG.setImage(playIMG);
    }

    public void forward() {
        mediaPlayer.seek(mediaPlayer.getCurrentTime().add(Duration.seconds(10)));
    }

    public void backward() {
        mediaPlayer.seek(mediaPlayer.getCurrentTime().add(Duration.seconds(-10)));
    }

    public void volumeIconfn() {
        myVolSlider.OnButton();
    }

    public void fileChoose() {
        mediaPlayer.pause();
        pausedIMG.setImage(playIMG);
        fileChooser = new FileChooser();
        fileChooser.setTitle("Choose File");
        fileChooser.getExtensionFilters().addAll(
                new ExtensionFilter("Media Files", "*.mp3", "*.wav", "*.mp4"),
                new ExtensionFilter("Video Files", "*.mp4"),
                new ExtensionFilter("Audio Files", "*.mp3", "*.wav"));
        file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            nextBtn.setVisible(false);
            prevBtn.setVisible(false);
            mediaPlayer.stop();
            pausedIMG.setImage(pauseIMG);
            isPlaylist = 1;
            setPlayer();
        }
    }

    public void changeSpeed(ActionEvent ae) {
        mySpeedChange.OnOption();
    }

    public void showLabel() {
        MediaTitle.setText(title);
        MediaTitle.setVisible(true);
    }

    public void hideLabel() {
        MediaTitle.setVisible(false);
    }

    public void addOnePlaylist() {
        mp.handle();
    }

    public void playByTheList() {
        if (mp.media.size() > 0) {
            isPlaylist = 1;
            mediaPlayer.stop();
            nextBtn.setVisible(true);
            prevBtn.setVisible(true);
            file = mp.media.get(0);
            setPlayer();
        }
    }

    public void next() {
        mp.mediaIndex++;
        mediaPlayer.stop();
        int sz = mp.media.size();
        int index = mp.mediaIndex;
        file = mp.media.get(index % sz);
        setPlayer();
    }

    public void previous() {
        mp.mediaIndex--;
        mediaPlayer.stop();
        int sz = mp.media.size();
        int index = mp.mediaIndex;
        file = mp.media.get(index % sz);
        setPlayer();
    }
}

//MyTimeSlider class controlls and manages the time slider of the current playing media
class MyTimeSlider {
    @FXML
    MediaPlayer mediaPlayer;
    @FXML
    private Label playTime;
    @FXML
    public Slider timeSlider;

    public MyTimeSlider(MediaPlayer m, Label l, Slider s) {
        this.mediaPlayer = m;
        this.playTime = l;
        this.timeSlider = s;
        mediaPlayer.currentTimeProperty().addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable ob) {
                updatesValues();
            }
        });
        timeSlider.valueProperty().addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable ob) {
                if (timeSlider.isPressed()) {
                    mediaPlayer.seek(mediaPlayer.getMedia().getDuration().multiply(timeSlider.getValue() / 100));
                }
            }
        });
    }

    private static String formatTime(Duration elapsed, Duration duration) {
        int intElapsed = (int) Math.floor(elapsed.toSeconds());
        int elapsedHours = intElapsed / (60 * 60);
        if (elapsedHours > 0) {
            intElapsed -= elapsedHours * 60 * 60;
        }
        int elapsedMinutes = intElapsed / 60;
        int elapsedSeconds = intElapsed - elapsedHours * 60 * 60
                - elapsedMinutes * 60;

        if (duration.greaterThan(Duration.ZERO)) {
            int intDuration = (int) Math.floor(duration.toSeconds());
            int durationHours = intDuration / (60 * 60);
            if (durationHours > 0) {
                intDuration -= durationHours * 60 * 60;
            }
            int durationMinutes = intDuration / 60;
            int durationSeconds = intDuration - durationHours * 60 * 60 - durationMinutes * 60;
            if (durationHours > 0) {
                return String.format("%d:%02d:%02d/%d:%02d:%02d",
                        elapsedHours, elapsedMinutes, elapsedSeconds,
                        durationHours, durationMinutes, durationSeconds);
            } else {
                return String.format("%02d:%02d/%02d:%02d",
                        elapsedMinutes, elapsedSeconds, durationMinutes,
                        durationSeconds);
            }
        } else {
            if (elapsedHours > 0) {
                return String.format("%d:%02d:%02d", elapsedHours,
                        elapsedMinutes, elapsedSeconds);
            } else {
                return String.format("%02d:%02d", elapsedMinutes,
                        elapsedSeconds);
            }
        }
    }

    protected void updatesValues() {
        Platform.runLater(new Runnable() {
            public void run() {
                playTime.setText(formatTime(mediaPlayer.getCurrentTime(), mediaPlayer.getTotalDuration()));
                timeSlider.setValue(mediaPlayer.getCurrentTime().toMillis() /
                        mediaPlayer.getTotalDuration().toMillis()
                        * 100);
            }
        });
    }
}

//MyVolSlider class controlls and manages the Volume slider of the current playing media
class MyVolSlider {
    @FXML
    Slider volSlider;
    @FXML
    private ImageView volIMG;
    @FXML
    private MediaPlayer mediaPlayer;
    Image vol;
    Image muted;

    public MyVolSlider(Slider v, ImageView i, MediaPlayer m) {
        this.volSlider = v;
        this.volIMG = i;
        this.mediaPlayer = m;
        vol = new Image("file:res/vol.png");
        muted = new Image("file:res/mute.png");
        volSlider.valueProperty().addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable arg0) {
                if (volSlider.isPressed()) {
                    mediaPlayer.setVolume(volSlider.getValue() / 100);
                }
                if (volSlider.getValue() == 0.0) {
                    volIMG.setImage(muted);
                } else {
                    volIMG.setImage(vol);
                }
            }
        });
    }

    public void OnButton() {
        if (mediaPlayer.isMute()) {
            volIMG.setImage(vol);
            mediaPlayer.setMute(false);
        } else if (volSlider.getValue() == 0.0) {
            volIMG.setImage(vol);
            volSlider.setValue(50);
            mediaPlayer.setVolume(volSlider.getValue() / 100);
        } else {
            volIMG.setImage(muted);
            mediaPlayer.setMute(true);
        }
    }

    public void onUPclick() {
        double x = volSlider.getValue();
        double y = x + 10;
        if (x > 90) {
            y = 100;
        }
        volSlider.setValue(y);
        mediaPlayer.setVolume(volSlider.getValue() / 100);
    }

    public void onDOWNclick() {
        double x = volSlider.getValue();
        double y = x - 10;
        if (x < 10) {
            y = 0;
        }
        volSlider.setValue(y);
        mediaPlayer.setVolume(volSlider.getValue() / 100);
    }
}

//MyPlayPause class controlls and manages the current state of the playing media
class MyPlayPause {
    @FXML
    private Button PlayButton;
    @FXML
    private ImageView pausedIMG;
    @FXML
    private MediaPlayer mediaPlayer;
    Image playIMG, pauseIMG;

    public MyPlayPause(MediaPlayer m, ImageView i, Button b) {
        this.mediaPlayer = m;
        this.pausedIMG = i;
        this.PlayButton = b;
        playIMG = new Image("file:res/play.png");
        pauseIMG = new Image("file:res/pause.png");
    }

    public void OnButton() {
        Status status = mediaPlayer.getStatus();
        if (status == status.PLAYING) {
            if (mediaPlayer.getCurrentTime().greaterThanOrEqualTo(mediaPlayer.getTotalDuration())) {
                mediaPlayer.seek(mediaPlayer.getStartTime());
                mediaPlayer.play();
            } else {
                mediaPlayer.pause();

                pausedIMG.setImage(playIMG);
            }
        }
        if (status == Status.HALTED || status == Status.STOPPED || status == Status.PAUSED) {
            mediaPlayer.play();
            pausedIMG.setImage(pauseIMG);
        }
    }
}

//MySpeedChange class controlls and manages the speed of the current playing media
class MySpeedChange {
    @FXML
    private ComboBox<String> speedControl;
    @FXML
    private MediaPlayer mediaPlayer;

    public MySpeedChange(ComboBox<String> c, MediaPlayer m) {
        this.mediaPlayer = m;
        this.speedControl = c;
        if (speedControl.getItems().isEmpty())
            for (int i = 25; i <= 200; i += 25) {
                speedControl.getItems().add(Integer.toString(i) + "%");
            }
        speedControl.setValue("100%");
    }

    public void OnOption() {
        if (speedControl.getValue() == null) {
            mediaPlayer.setRate(1);
        } else {
            mediaPlayer.setRate(
                    Integer.parseInt(speedControl.getValue().substring(0, speedControl.getValue().length() - 1))
                            * 0.01);
        }
    }
}

//MyPlaylist class controlls and manages the user defined playlist of media with a queue
class MyPlaylist {
    public ArrayList<File> media;
    public int mediaIndex = 0;
    @FXML
    Button play, add;
    @FXML
    VBox vBox;
    FileChooser fileChooser;
    Stage stage;
    File file;
    MediaPlayer mediaPlayer;
    @FXML
    Button p, n;
    HBox h;

    public MyPlaylist(Button a, Button p, VBox v, MediaPlayer m, Stage s) {
        this.play = p;
        this.add = a;
        this.vBox = v;
        this.mediaPlayer = m;
        this.stage = s;
        media = new ArrayList<File>();
    }

    public void handle() {
        fileChooser = new FileChooser();
        fileChooser.setTitle("Choose File");
        fileChooser.getExtensionFilters().addAll(
                new ExtensionFilter("Media Files", "*.mp3", "*.wav", "*.mp4"),
                new ExtensionFilter("Video Files", "*.mp4"),
                new ExtensionFilter("Audio Files", "*.mp3", "*.wav"));
        file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            media.add(file);
            String fileName = file.getName();
            String extension = "";
            int i = fileName.lastIndexOf('.');
            extension = fileName.substring(0, i);
            Label l = new Label(extension);
            Font f = Font.font("Arial", FontWeight.BOLD, 16);
            l.setFont(f);
            h = new HBox();
            h.getChildren().addAll(l);
            vBox.getChildren().addAll(h);
        }
    }
}