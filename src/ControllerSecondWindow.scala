import InitSubScene._
import Utils._
import javafx.fxml.FXML
import javafx.scene.SubScene
import javafx.scene.control._
import javafx.scene.layout.AnchorPane

class ControllerSecondWindow {

  @FXML private var subScene1:SubScene = _
  @FXML private var button1:Button = _
  @FXML private var button2:Button = _
  @FXML private var mouseUp:Button = _
  @FXML private var mouseDown:Button = _
  @FXML private var grow:Button = _
  @FXML private var decrease:Button = _
  @FXML private var pane1:AnchorPane = _


  @FXML
  def initialize(): Unit = {
    InitSubScene.subScene.widthProperty.bind(subScene1.widthProperty)
    InitSubScene.subScene.heightProperty.bind(subScene1.heightProperty)
    subScene1.setRoot(InitSubScene.root)
    pane1.setMinSize(1000.0,1000.0)
  }

  //method automatically invoked after the @FXML fields have been injected
  //  @FXML
    def Sepia(): Unit = {
    FxApp.tree = FxApp.images.mapColourEffect(applySepiaToList, FxApp.tree)
    }

  def RemoveGreen(): Unit = {
    FxApp.tree = FxApp.images.mapColourEffect(removeGreen, FxApp.tree)
  }

  def GrowTree(): Unit = {
    FxApp.tree = FxApp.images.scaleOctree(2,FxApp.tree)
  }

  def HalveTree(): Unit = {
    FxApp.tree = FxApp.images.scaleOctree(0.5,FxApp.tree)
  }

  def MouseUp(): Unit = {
      camVolume.setTranslateX(camVolume.getTranslateX + 2)
  }

  def MouseDown(): Unit = {
          camVolume.setTranslateX(camVolume.getTranslateX - 2)
  }

}
