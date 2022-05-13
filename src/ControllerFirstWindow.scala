import scala.util.{Try, Success, Failure}
import InitSubScene._
import Utils._
import javafx.fxml._
import javafx.scene.control._
import javafx.scene._
import javafx.stage._

class ControllerFirstWindow {

  @FXML private var button1: Button = _
  @FXML private var label1: Label = _
  @FXML private var radio1:RadioButton = _
  @FXML private var radio2:RadioButton = _
  @FXML private var radio3:RadioButton = _
  @FXML private var radio4:RadioButton = _
//  @FXML private var labelErr: Label = _
//  @FXML private var labelErr2: Label = _
//  @FXML private var labelErr3: Label = _
  @FXML private var slider1: Slider = _

  def setSizeFirst(): Unit = {
          button1.setVisible(true)
          label1.setVisible(true)
        }

  def OnButton1Clicked(): Unit = {

    val stage:Stage = new Stage()
    val fileChooser:FileChooser = new FileChooser()
    fileChooser.setTitle("Open Resource File")

    val path = fileChooser.showOpenDialog(stage).getName
    println(s"${path}")

    val fileObjects = readFromFile(s"src/$path")
    FxApp.images = new ImageCollection(worldRoot,fileObjects)

//    val size = Integer.parseInt(textField1.getText)
    val size = radio1.get

    FxApp.tree = makeTree(new Placement((0,0,0),size), FxApp.images.objects,FxApp.images.worldRoot)
//    FxApp.images.updateWorld

    val fxmlLoader2 = new FXMLLoader(getClass.getResource("Controller2.fxml"))
    val mainViewRoot: Parent = fxmlLoader2.load()

    button1.getScene.setRoot(mainViewRoot)
  }

}
