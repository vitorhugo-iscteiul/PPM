import javafx.fxml.FXMLLoader

import scala.io.Source
import javafx.scene.{Group, Node, Parent, PerspectiveCamera, Scene, SceneAntialiasing, SubScene}
import javafx.scene.paint.{Color, Material, PhongMaterial}
import javafx.scene.shape.{Box, Cylinder, DrawMode, Line, Shape3D}
import javafx.stage.Stage
import scala.util.{Try, Success, Failure}
import scala.annotation.tailrec

object Utils {
  //Auxiliary types
  type Point = (Double, Double, Double)
  type Size = Double
  type Placement = (Point, Size) //1st point: origin, 2nd point: size

  //Shape3D is an abstract class that extends javafx.scene.Node
  //Box and Cylinder are subclasses of Shape3D
  type Section = (Placement, List[Node]) //example: ( ((0.0,0.0,0.0), 2.0), List(new Cylinder(0.5, 1, 10)))

  //T6 desenvolver uma text-based User Interface permitindo escolher o ficheiro
  //de configuração, lançar (uma única vez antes de terminar a execução) a
  //visualização do ambiente 3D e aplicar os métodos desenvolvido (p.e.
  //scaleOctree);

    def showPrompt(): Unit = {
    println("Please state the name of the configuration file without the extension: ")
  }

  def printChoose(): Unit = {
    println("Please choose a number: ")
    println("1 - OcScale")
    println("2 - Colour effect ")
    println("Select any other number to show the output")
    println("Number:")
  }

  //------------------------------------------------------------------------//

  def newColour(red: Int, green: Int, blue: Int): PhongMaterial = {
    val new_colour = new PhongMaterial()

    val newRed = red.min(255)
    val newGreen = green.min(255)
    val newBlue = blue.min(255)

    new_colour.setDiffuseColor(Color.rgb(newRed, newGreen, newBlue))
    new_colour
  }

  //--------------------------------------------------------------------------------//

  def applySepiaToList(color: Color): Color = {
    val newRed = (0.4 * color.getRed +  0.77 * color.getGreen + 0.20 * color.getBlue).min(1.0)
    val newGreen = (0.35 * color.getRed +  0.69 * color.getGreen + 0.17 * color.getBlue).min(1.0)
    val newBlue = (0.27 * color.getRed +  0.53 * color.getGreen  + 0.13 * color.getBlue).min(1.0)

    val newColor = new Color(newRed,newGreen,newBlue,color.getOpacity)
    newColor
  }

  def removeGreen(color: Color): Color = {
    val newColor = new Color(color.getRed,0.0,color.getBlue,color.getOpacity)
    newColor
  }

  def isItInt(s : String):Boolean = {
    def makeInt(s: String): Try[Int] = {
      Try(s.trim.toInt)
    }
    makeInt(s) match {
      case Success(_) => true
      case Failure(_) => false
    }
  }

 def readFromFile(file: String): List[Node] = {
   val bufferedSource = Source.fromFile(file)

   var boxArr: Array[Shape3D] = Array()
   var cyArr: Array[Shape3D] = Array()

   if (bufferedSource == null) {
     println("The file chosen does not contain the necessary information. Please try another")
   }

   for (line <- bufferedSource.getLines) {

     if (!line.isEmpty || !line.isBlank) {

       val new_item = line.split(" ")
       val objName = new_item(0)

       val temp = new_item(1).replaceAll("\\(", "").replaceAll("\\)", "").split(",")
       val colourList = temp.toList.map(x => x.toInt)

       if (new_item.size > 2) {
         objName match {
           case "Cylinder" => cyArr = cyArr :+ new Cylinder(0.5, 1, 10)
             cyArr.last.setTranslateX(new_item(2).toInt)
             cyArr.last.setTranslateY(new_item(3).toInt)
             cyArr.last.setTranslateZ(new_item(4).toInt)
             cyArr.last.setScaleX(new_item(5).toDouble)
             cyArr.last.setScaleY(new_item(6).toDouble)
             cyArr.last.setScaleZ(new_item(7).toDouble)
             cyArr.last.setMaterial(newColour(colourList(0), colourList(1), colourList(2)))

           case "Box" => boxArr = boxArr :+ new Box(1, 1, 1)
             boxArr.last.setTranslateX(new_item(2).toInt)
             boxArr.last.setTranslateY(new_item(3).toInt)
             boxArr.last.setTranslateZ(new_item(4).toInt)
             boxArr.last.setScaleX(new_item(5).toDouble)
             boxArr.last.setScaleY(new_item(6).toDouble)
             boxArr.last.setScaleZ(new_item(7).toDouble)
             boxArr.last.setMaterial(newColour(colourList(0), colourList(1), colourList(2)))

           case _ => println("This line does not match the necessary requirements")
         }
       }
       else {
         objName match {
           case "Cylinder" => cyArr = cyArr :+ new Cylinder(0.5, 1, 10)
             cyArr.last.setTranslateX(0)
             cyArr.last.setTranslateY(0)
             cyArr.last.setTranslateZ(0)
             cyArr.last.setScaleX(1)
             cyArr.last.setScaleY(1)
             cyArr.last.setScaleZ(1)
             cyArr.last.setMaterial(newColour(colourList(0), colourList(1), colourList(2)))

           case "Box" => boxArr = boxArr :+ new Box(1, 1, 1)
             boxArr.last.setTranslateX(0)
             boxArr.last.setTranslateY(0)
             boxArr.last.setTranslateZ(0)
             boxArr.last.setScaleX(1)
             boxArr.last.setScaleY(1)
             boxArr.last.setScaleZ(1)
             boxArr.last.setMaterial(newColour(colourList(0), colourList(1), colourList(2)))

           case _ => println("This line does not match the necessary requirements")
         }
       }
     }
   }
   val objects: List[Node] = cyArr.toList.concat(boxArr.toList)
   bufferedSource.close
   objects

 }

  //AUXILIAR
  //FUNCAO PARA GERAR A LISTA DE OBJECTOS QUE ESTEJAM CONTIDOS DENTRO DE DETERMINADO BOX
  def getObjectsInsideBox(box: Box, listObject: List[Node], worldRoot: Group): List[Node] =
    listObject match {
      case Nil => Nil
      case head :: tail => {
        if (box.getBoundsInParent.contains(head.asInstanceOf[Shape3D].getBoundsInParent))
          head :: getObjectsInsideBox(box, tail,worldRoot)
        else {
//          worldRoot.getChildren.remove(head)
          worldRoot.getChildren.remove(head)
          getObjectsInsideBox(box, tail,worldRoot)
        }
      }
    }


  def createBox(placement: Placement):Box = {
    val sizeDaCox = placement._2
    val box = new Box(sizeDaCox, sizeDaCox, sizeDaCox)

    box.setTranslateX(placement._1._1 + sizeDaCox / 2)
    box.setTranslateY(placement._1._2 + sizeDaCox / 2)
    box.setTranslateZ(placement._1._3 + sizeDaCox / 2)
    box.setMaterial(newColour(255,0,0))
    box.setDrawMode(DrawMode.LINE)
    box
  }

  //FUNCAO PARA GERAR UM ELEMENTO DO TIPO BOX PARA UMA DETERMINADA SECCAO
  def createNextBoxes(placement: Placement): List[Box] = {
    val size = placement._2 / 2.0
    val x = placement._1._1
    val y = placement._1._2
    val z = placement._1._3
    //(0,0,0)
    val box1: Box = createBox(((x, y, z), size))
    //(0,size,0)
    val box2: Box = createBox(((x, y + size, z), size))
    //(0,0,size)
    val box3: Box = createBox(((x, y, z + size), size))
    //(size,0,0)
    val box4: Box = createBox(((x + size, y, z), size))
    //(0,size,size)
    val box5: Box = createBox(((x, y + size, z + size), size))
    //(size, 0,size)
    val box6: Box = createBox(((x + size, y, z + size), size))
    //(size,size,0)
    val box7: Box = createBox(((x + size, y + size, z), size))
    //(size,size,size)
    val box8: Box = createBox(((x + size, y + size, z + size), size))

    box1 :: box2 :: box3 :: box4 :: box5 :: box6 :: box7 :: box8 :: List()
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
        case head :: tail =>
          if (b.getBoundsInParent.intersects(head.asInstanceOf[Shape3D].getBoundsInParent) //caso de intersetar e nao conter
            && !b.getBoundsInParent.contains(head.asInstanceOf[Shape3D].getBoundsInParent))
            true
          else
            runThroughObjects(b, tail)
      }
    listBoxes match {
      case Nil => false
      case head :: tail =>
        if (runThroughObjects(head, listObjects))
          true
        else childNodesIntersect(tail, listObjects)
    }
  }

  //---------------------------------------------------------------------//

  /*
    T2 criar uma octree de acordo com os modelos gráficos previamente carregados e permitir
    a sua visualização (as partições espaciais são representadas com wired cubes). A octree
    oct1 presente no código fornecido poderá ajudar na interpretação;
    */

  //FUNCAO PARA GERAR TODAS AS OCTREES DAS 8 SECCOES QUE PROSSEGUEM DETERMINADO NODO
  def generateChild(listBoxes: List[Box], listObjects: List[Node], worldRoot: Group): List[Octree[Placement]] =
    listBoxes match {
      case Nil => Nil
      case head :: tail => {
        val secX = head.getTranslateX - head.getHeight/2
        val secY = head.getTranslateY - head.getHeight/2
        val secZ = head.getTranslateZ - head.getHeight/2

        //nodo filho nao tem nenhuma lista contida então é empty
        val boxElements = getObjectsInsideBox(head,listObjects,worldRoot)

        if (boxElements.isEmpty)
          OcEmpty :: generateChild(tail,listObjects,worldRoot)
        else {
          if(!worldRoot.getChildren.contains(head))
            worldRoot.getChildren.add(head)
          //nodo filho tem elementos contidos entao vai verificar se os filhos desse filho intersectam alguma coisa
          val listNextBoxes = createNextBoxes((secX, secY, secZ),head.getHeight)

          //se os filhos do nodo filho intersectarem entao o nodo filho é uma folha
          if (childNodesIntersect(listNextBoxes, boxElements)) {
            val sec:Section = new Section(((secX,secY,secZ),head.getHeight),boxElements)
            OcLeaf(sec) :: generateChild(tail,listObjects,worldRoot)
          } else {
            //caso os filhos nao interceptem irá ser necessario fazer com que sejam criados os nodos filhos
            OcNode[Placement](new Placement((secX, secY, secZ),head.getWidth),generateChild(listNextBoxes,listObjects,worldRoot).apply(0),generateChild(listNextBoxes,listObjects,worldRoot).apply(1),
              generateChild(listNextBoxes,listObjects, worldRoot).apply(2),generateChild(listNextBoxes,listObjects,worldRoot).apply(3),generateChild(listNextBoxes,listObjects,worldRoot).apply(4),
              generateChild(listNextBoxes,listObjects,worldRoot).apply(5),generateChild(listNextBoxes,listObjects,worldRoot).apply(6),generateChild(listNextBoxes,listObjects,worldRoot).apply(7),
            ):: generateChild(tail,listObjects,worldRoot)
          }
        }
      }
    }


  //Funcao para criar a OcTree como deve ser
  def makeTree(p: Placement, list: List[Node], worldRoot: Group): Octree[Placement] = {

    val box = createBox(p)
    worldRoot.getChildren.add(box)
    //WIRED BOX SO ACEITE OBJECTOS CONTIDOS SE NAO CONTIVER CORTA FORA OS OBJECTOS

    val wiredListObjects:List[Node] = getObjectsInsideBox(box,list,worldRoot)    //LISTA OBJECTOS DA WIREBOX

    if(wiredListObjects.isEmpty) return OcEmpty       //SOU VAZIO ? SOU OCEMPTY

    val listNextBoxes = createNextBoxes(p) //Lista das 8 proximas caixas com objectos contidos

    if(childNodesIntersect(listNextBoxes,wiredListObjects)){ //FUNCAO QUE RECEBE SECCOES E VAI VER LISTA DE OBJECTOS DA ROOT E VÊ SE ALGUM OBJECTO É INTERSECTADo MAS NAO CONTIDO
      val fatherOcleaf:Octree[Placement] = OcLeaf((p,wiredListObjects))
      return fatherOcleaf // CASO ALGUM DER TRUE ELE ACABA E O PAI É FOLHA
    }

    val childPopulate:List[Octree[Placement]] = generateChild(listNextBoxes,wiredListObjects,worldRoot) //FUNCAO PARA GERAR ARVORES A PARTIR DAS SECCOES DOS FILHOS

    //RETORNO FINAL É A OCNODE(PLACEMENTE WIREBOX, GERAR_FILHO(SECCAO FILHO 1), GERAR FILHO(SECCAO FILHO 2),...., GERAR_FILHO(SECCAO FILHO 8)

    val finalTree:Octree[Placement] = OcNode(p,childPopulate.apply(0),childPopulate.apply(1),childPopulate.apply(2),
      childPopulate.apply(3),childPopulate.apply(4),childPopulate.apply(5),childPopulate.apply(6),childPopulate.apply(7))
    finalTree
  }

  //-------------------------------------------------------------------//

  def treePlacement(oct: Octree[Placement]): Placement = {
    oct match {
      case OcEmpty => null

      case OcNode(coords,_,_,_,_,_,_,_,_) =>
        val placement: Placement = (coords._1, coords._2)
        placement

      case OcLeaf(section:Section) =>
        val placement: Placement = (section._1._1, section._1._2)
        placement
    }

  }

  def scaleList(fact : Double, list: List[Node]) : List[Node] = {
    list match {
      case Nil => Nil
      case head :: tail =>{
        head.setTranslateX(head.getTranslateX * fact)
        head.setTranslateY(head.getTranslateY * fact)
        head.setTranslateZ(head.getTranslateZ * fact)

        head.setScaleX(head.getScaleX * fact)
        head.setScaleY(head.getScaleY * fact)
        head.setScaleZ(head.getScaleZ * fact)
      }
        head :: scaleList(fact, tail)
    }
  }


  //-------------------------------------------------------------------------------------//

  //T3 permitir que durante a visualização e mediante movimento da câmera (o
  //movimento é obtido, no código fornecido, clicando no botão esquerdo do
  //rato), sejam visualmente identificados, através de alteração da cor da
  //partição, as partições espaciais da octree que sejam visíveis a partir da
  //câmera (i.e., que intersetam o seu volume de visualização). O código dado
  //também fornece uma third person view (canto inferior direito) que permite
  //visualizar a octree de diferentes perspetivas (através do rato),
  //independentemente da posição da câmera;

  //função para que as partições mudem de cor quando a camera passar por cima das mesmas

  @tailrec
  def changeSectionColorIfCamInside(partitionsList: List[Node], camVolume: Cylinder): List[Node] = {
    partitionsList match {
      case List() => Nil
      case head :: tail =>
        if (camVolume.getBoundsInParent.intersects(head.getBoundsInParent) && (head.isInstanceOf[Box] && head.asInstanceOf[Box].getDrawMode == DrawMode.LINE)) {
          head.asInstanceOf[Shape3D].setMaterial(newColour(0,255,0))
        } else if (head.isInstanceOf[Box] && head.asInstanceOf[Box].getDrawMode == DrawMode.LINE)
          head.asInstanceOf[Shape3D].setMaterial(newColour(255,0,0))
        changeSectionColorIfCamInside(tail, camVolume)
    }
  }

}