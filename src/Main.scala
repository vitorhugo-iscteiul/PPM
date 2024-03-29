
//import Utils.{Placement, boxGenerator, getList}

import Utils._
import javafx.application.Application
import javafx.collections.ObservableList
import javafx.fxml.FXMLLoader
import javafx.geometry.{Insets, Pos}
import javafx.scene._
import javafx.scene.layout.StackPane
import javafx.scene.paint.{Color, PhongMaterial}
import javafx.scene.shape._
import javafx.scene.transform.Rotate
import javafx.stage.Stage

import java.util.stream.Collectors
//import scala.::
import scala.annotation.tailrec

class Main extends Application {

//  //Auxiliary types
//  type Point = (Double, Double, Double)
//  type Size = Double
//  type Placement = (Point, Size) //1st point: origin, 2nd point: size
//
//  //Shape3D is an abstract class that extends javafx.scene.Node
//  //Box and Cylinder are subclasses of Shape3D
//  type Section = (Placement, List[Node]) //example: ( ((0.0,0.0,0.0), 2.0), List(new Cylinder(0.5, 1, 10)))

  /*
    Additional information about JavaFX basic concepts (e.g. Stage, Scene) will be provided in week7
   */
  override def start(stage: Stage): Unit = {

//    Get and print program arguments (args: Array[String])
    val params = getParameters
    println("Program arguments:" + params.getRaw)

    showPrompt()
    val userInput = getUserInput()

    //Materials to be applied to the 3D objects
    val redMaterial = new PhongMaterial()
    redMaterial.setDiffuseColor(Color.rgb(150, 0, 0))

    val greenMaterial = new PhongMaterial()
    greenMaterial.setDiffuseColor(Color.rgb(0, 255, 0))

    val blueMaterial = new PhongMaterial()
    blueMaterial.setDiffuseColor(Color.rgb(0, 0, 150))

    //3D objects
    val lineX = new Line(0, 0, 200, 0)
    lineX.setStroke(Color.BLACK)

    val lineY = new Line(0, 0, 0, 200)
    lineY.setStroke(Color.BLACK)

    val lineZ = new Line(0, 0, 200, 0)
    lineZ.setStroke(Color.BLACK)
    lineZ.getTransforms().add(new Rotate(-90, 0, 0, 0, Rotate.Y_AXIS))

    val camVolume = new Cylinder(10, 50, 10)
    camVolume.setTranslateX(1)
    camVolume.getTransforms().add(new Rotate(45, 0, 0, 0, Rotate.X_AXIS))
    camVolume.setMaterial(blueMaterial)
    camVolume.setDrawMode(DrawMode.LINE)

    val wiredBox = new Box(32, 32, 32)
    wiredBox.setTranslateX(32 / 2)
    wiredBox.setTranslateY(32 / 2)
    wiredBox.setTranslateZ(32 / 2)
    wiredBox.setMaterial(redMaterial)
    wiredBox.setDrawMode(DrawMode.LINE)

    val cylinder1 = new Cylinder(0.5, 1, 10)
    cylinder1.setTranslateX(2)
    cylinder1.setTranslateY(2)
    cylinder1.setTranslateZ(2)
    cylinder1.setScaleX(2)
    cylinder1.setScaleY(2)
    cylinder1.setScaleZ(2)
    cylinder1.setMaterial(greenMaterial)

    val box1 = new Box(1, 1, 1) //
    box1.setTranslateX(5)
    box1.setTranslateY(5)
    box1.setTranslateZ(5)
    box1.setMaterial(greenMaterial)

//    val helper = new Utils

//    val objects: List[Node] = Utils.readFromFile("src/conf.txt", List(redMaterial, greenMaterial, blueMaterial)) //relative path
    val objects: List[Node] = readFromFile(s"src/$userInput.txt") //relative path

    // 3D objects (group of nodes - javafx.scene.Node) that will be provide to the subScene
    val worldRoot: Group = new Group(wiredBox, camVolume, lineX, lineY, lineZ)

    println(s"worldroot size before  ${worldRoot.getChildren.size()}")

    //Função para alimentar o WorlGroup com os dados lidos no ficheiro
//    def fillWorldGroup(nodes: List[Node], world: Group): Unit = {
//      nodes match {
//        case Nil => Nil
//        case head :: tail => {
//          world.getChildren.add(head)
//          fillWorldGroup(tail, world)
//        }
//      }
//    }
//
//
//    fillWorldGroup(objects, worldRoot) //Comando para preencher o worldRoot com os elementos lidos no ficheiro

    //Adicionar ao WorlGroup os Node obtidos no ficheiro
    objects.map(x => worldRoot.getChildren.add(x))

    // Camera
    val camera = new PerspectiveCamera(true)

    val cameraTransform = new CameraTransformer
    cameraTransform.setTranslate(0, 0, 0)
    cameraTransform.getChildren.add(camera)
    camera.setNearClip(0.1)
    camera.setFarClip(10000.0)

    camera.setTranslateZ(-500)
    camera.setFieldOfView(20)
    cameraTransform.ry.setAngle(-45.0)
    cameraTransform.rx.setAngle(-45.0)
    worldRoot.getChildren.add(cameraTransform)

    // SubScene - composed by the nodes present in the worldRoot
    val subScene = new SubScene(worldRoot, 800, 600, true, SceneAntialiasing.BALANCED)
    subScene.setFill(Color.WHITE)
    subScene.setCamera(camera)

    // CameraView - an additional perspective of the environment
    val cameraView = new CameraView(subScene)
    cameraView.setFirstPersonNavigationEabled(true)
    cameraView.setFitWidth(350)
    cameraView.setFitHeight(225)
    cameraView.getRx.setAngle(-45)
    cameraView.getT.setZ(-10)
    cameraView.getT.setY(-50)
    cameraView.getCamera.setTranslateZ(-50)
    cameraView.startViewing

    // Position of the CameraView: Right-bottom corner
    StackPane.setAlignment(cameraView, Pos.TOP_LEFT)
    StackPane.setMargin(cameraView, new Insets(3))

    // Scene - defines what is rendered (in this case the subScene and the cameraView)
    val root = new StackPane(subScene, cameraView)
    subScene.widthProperty.bind(root.widthProperty)
    subScene.heightProperty.bind(root.heightProperty)

    val scene = new Scene(root, 810, 610, true, SceneAntialiasing.BALANCED)

    //T3 permitir que durante a visualização e mediante movimento da câmera (o
    //movimento é obtido, no código fornecido, clicando no botão esquerdo do
    //rato), sejam visualmente identificados, através de alteração da cor da
    //partição, as partições espaciais da octree que sejam visíveis a partir da
    //câmera (i.e., que intersetam o seu volume de visualização). O código dado
    //também fornece uma third person view (canto inferior direito) que permite
    //visualizar a octree de diferentes perspetivas (através do rato),
    //independentemente da posição da câmera;

    //função para que os elementos mudem de cor quando a camera passar por cima d
    @tailrec
    def isInsideObj(partitionsList: List[Node], camVolume: Cylinder): List[Node] = {
      partitionsList match {
        case List() => Nil
        case head :: tail => {
          if (camVolume.getBoundsInParent.intersects(head.getBoundsInParent) && (head.isInstanceOf[Box] && head.asInstanceOf[Box].getDrawMode == DrawMode.LINE)) {
            head.asInstanceOf[Shape3D].setMaterial(greenMaterial)
          } else if (head.isInstanceOf[Box] && head.asInstanceOf[Box].getDrawMode == DrawMode.LINE)
            head.asInstanceOf[Shape3D].setMaterial(redMaterial)
          isInsideObj(tail, camVolume)
        }
      }
    }


    //MOUSE LOOP
    //Mouse left click interaction
    scene.setOnMouseClicked((event) => {
      camVolume.setTranslateX(camVolume.getTranslateX + 2)
      worldRoot.getChildren.removeAll()
      //comando para activar a mudança de cor com a câmara
      val list = worldRoot.getChildren.toArray().toList.asInstanceOf[List[Node]] //SHAFSDHAFSADFJSDFLSAF
      isInsideObj(list, camVolume)

    })

    //setup and start the Stage
    stage.setTitle("PPM Project 21/22")
    val fxmlLoader = new FXMLLoader(getClass.getResource("Controller.fxml"))
    val mainViewRoot: Parent = fxmlLoader.load()
    val scene2 = new Scene(mainViewRoot)


    stage.setScene(scene)


//    stage.setScene(scene2)
//    stage.show

    //T4


    //TODO T4
    // scaleOctree(fact:Double, oct:Octree[Placement]):Octree[Placement]
    //operação de ampliação/redução de uma octree e dos modelos gráficos nela
    //inseridos, segundo o fator fornecido (assumir somente a utilização dos
    //fatores 0.5 e 2 – para controlar a complexidade). A ampliação poderá resultar
    //numa octree com dimensão máxima superior a 32 unidades;

    def callScaleOctree(fact:Double, oct:Octree[Placement]):Octree[Placement] = {
      val placement:Placement = treePlacement(oct)
      val newTree:Octree[Placement] = scaleOctree(fact,oct)

           newTree match {
             case OcEmpty => OcEmpty

             case OcNode(coords, t1, t2, t3, t4, t5, t6, t7, t8) =>
            val alteredBox = boxGenerator(coords) // caixa ampliada ou reduzida

            if(!worldRoot.getChildren.contains(alteredBox))
              worldRoot.getChildren.add(alteredBox)

            val originalBox = boxGenerator(placement) //caixa  tamanho original
            val scaledList = scaleList(fact, getList(objects, originalBox, 1))

          case OcLeaf(sec : Section) =>
            val coords: Placement = (sec._1._1, sec._1._2 * fact)
            val alteredBox = boxGenerator(coords)

            if(!worldRoot.getChildren.contains(alteredBox))
              worldRoot.getChildren.add(alteredBox)

            val originalBox = boxGenerator(sec._1) //caixa  tamanho original
            val scaledList = scaleList(fact, getList(objects, originalBox, 1))

            makeTree(placement, alteredBox, scaledList)
          }
        }

      }

      def scaleList(fact : Double, list: List[Node]) : List[Node] = {
        list match{
          case Nil => Nil
          case head :: tail => {
            if(head.isInstanceOf[Cylinder]) {
              head.asInstanceOf[Cylinder].setRadius(head.asInstanceOf[Cylinder].getRadius * fact)
              head.asInstanceOf[Cylinder].setHeight(head.asInstanceOf[Cylinder].getHeight * fact)
            }else {
              //Criar novo objeto box com escala modificada?
              val size = head.asInstanceOf[Box].getWidth
              head.setScaleX(size * fact)
              head.setScaleY(size * fact)
              head.setScaleZ(size * fact)
            }
            head :: scaleList(fact, tail)
          }
        }
      }

    //FUNCAO PARA VALIDAR SE ALGUM DOS 8 SECCOES QUE PROSSEGUEM UM DETERMINADO NODO IRAO INTERSECTAR MAS NAO CONTER ALGUM ELEMENTO
    //OU SEJA, VALIDA SE É POSSIVEL QUE O ELEMENTO CONTIDO PODERÁ VIR A SER PARTIDO EM 8 PARTES OU NAO
    //CASO SEJA POSSIVEL SER PARTIDO EM 8 PARTES ENTAO RETORNA TRUE PARA QUE O NODO "PAI" SAIBA QUE PODERA PROCEDER EM DIVIDIR-SE
    //CASO RETORNE FALSE O NODO PAI IRA RECEBER A INFORMAÇÃO DE QUE NAO SE PODERÁ REPARTIR E TERA DE SER ELE A FOLHA
    @tailrec
    def childNodesIntersect(listBoxes: List[Box], listObjects: List[Node]): Boolean = {
      @tailrec
      def runThroughObjects(b: Box, listObjects: List[Node]): Boolean =
        listObjects match {
          case Nil => false
          case head :: tail => {
            if (b.getBoundsInParent.intersects(head.asInstanceOf[Shape3D].getBoundsInParent) //caso de intersetar e nao conter
              && !b.getBoundsInParent.contains(head.asInstanceOf[Shape3D].getBoundsInParent))
              true
            else
              runThroughObjects(b, tail)
          }
        }
      listBoxes match {
        case Nil => false
        case head :: tail => {
          if (runThroughObjects(head, listObjects))
              true
           else childNodesIntersect(tail, listObjects)
        }
      }
    }

    //FUNCAO PARA GERAR TODAS AS OCTREES DAS 8 SECCOES QUE PROSSEGUEM DETERMINADO NODO
    def generateChild(listBoxes: List[Box], listObjects: List[Node]): List[Octree[Placement]] =
      listBoxes match {
        case Nil => Nil
        case head :: tail => {
          //nodo filho nao tem nenhuma lista contida então é empty
          val boxElements = helper.getList(listObjects,head,1)
          if (boxElements.isEmpty) OcEmpty :: generateChild(tail,listObjects)
          else {
            println(s" 44. elemetos do world ${worldRoot.getChildren.size()}")
            if(!worldRoot.getChildren.contains(head))
              worldRoot.getChildren.add(head)

            //nodo filho tem elementos contidos entao vai verificar se os filhos desse filho intersectam alguma coisa
            val listNextBoxes = helper.getNextBoxes((head.getTranslateX,head.getTranslateY,head.getTranslateZ),head.getHeight)
            //se os filhos do nodo filho intersectarem entao o nodo filho é uma folha
            if (helper.childNodesIntersect(listNextBoxes, boxElements)) {
              OcLeaf(new Section(((head.getTranslateX,head.getTranslateY,head.getTranslateZ),head.getHeight),boxElements)) :: generateChild(tail,listObjects)
            } else {
              //caso os filhos nao interceptem irá ser necessario fazer com que sejam criados os nodos filhos
              OcNode[Placement](new Placement((head.getTranslateX,head.getTranslateY, head.getTranslateZ),head.getWidth),generateChild(listNextBoxes,listObjects).apply(0),generateChild(listNextBoxes,listObjects).apply(1),
                generateChild(listNextBoxes,listObjects).apply(2),generateChild(listNextBoxes,listObjects).apply(3),generateChild(listNextBoxes,listObjects).apply(4),
                generateChild(listNextBoxes,listObjects).apply(5),generateChild(listNextBoxes,listObjects).apply(6),generateChild(listNextBoxes,listObjects).apply(7),
              ):: generateChild(tail,listObjects)
            }
          }
      }
      }

    //    //SECCOES COM COORDENADAS FIXAS QUE CONSTITUEM O 32 CUBO
    //    val size_cubo = wiredBox.getHeight / 2
    val placement1: Placement = ((0, 0, 0), 32.0)

    def mainChoose(): Unit = {
      printChoose()
      val userInput2 = getUserInputInt()

      userInput2 match {

        case 1 =>
        println(s" Please choose a factorial between 0.5 or 2")
          val userInputFact = getUserInputDouble
          val tree = makeTree(placement1, wiredBox, objects,worldRoot)
//          val scaledTree:Octree[Placement] =
            callScaleOctree(userInputFact, tree)

        case 2 =>
          println(s" Please choose a format color for your tree:")
          println(s" 1 - applySepiaToList")
          println(s" 2 - removeGreen")
          val userInputFunc = getUserInputInt()
          val tree = makeTree(placement1, wiredBox, objects,worldRoot)
          userInputFunc match {
            case 1 =>  val coloredTree:Octree[Placement] = mapColourEffect(applySepiaToList,tree)
            case 2 =>  val coloredTree:Octree[Placement] = mapColourEffect(removeGreen,tree)
            case _ => println("amigo, ou 1 ou 2 nada mais....burro...")
          }

        case 0 =>
          println(s" .....")
          val tree = makeTree(placement1, wiredBox, objects,worldRoot)

        case _ => println("Burro é um número que tens de escolher")

      }

    }


    //USER TEXT INTERFACE
    mainChoose()
    stage.show


//
//
//    println(s" areveres somos:  $tree ")
//    println(s"33. OcTree ${scaleOctree(2.0, tree)}")

    //example of bounding boxes (corresponding to the octree oct1) added manually to the world
//val b2 = new Box(8, 8, 8)
//translate because it is added by defaut to the coords (0,0,0)
//b2.setTranslateX(32 / 8)
//b2.setTranslateY(8 / 2)
//b2.setTranslateZ(8 / 2)
//b2.setMaterial(blueMaterial)
//b2.setDrawMode(DrawMode.LINE)
//
//val b3 = new Box(4, 4, 4)
//translate because it is added by defaut to the coords (0,0,0)
//b3.setTranslateX(4 / 2)
//b3.setTranslateY(4 / 2)
//b3.setTranslateZ(4 / 2)
//b3.setMaterial(blueMaterial)
//b3.setDrawMode(DrawMode.LINE)

//adding boxes b2 and b3 to the world
//      worldRoot.getChildren.add(b2)
//      worldRoot.getChildren.add(b3)

//val newObjects = helper.applySepiaToList(objects)
//    print(s" 65. lista com sepia $newObjects")

}

override def init(): Unit = {
  println("init")
}

override def stop(): Unit = {
  println("\n=== You have closed the application ===")
}

}

object FxApp {

  def main(args: Array[String]): Unit = {
    Application.launch(classOf[Main], args: _*)

  }
}
