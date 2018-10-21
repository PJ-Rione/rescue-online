package rioneviewer3;

import java.io.File;


import adf.agent.info.AgentInfo;
import javafx.scene.paint.Color;
import rescuecore2.standard.entities.Area;
import rescuecore2.standard.entities.Blockade;
import rescuecore2.standard.entities.Building;
import rescuecore2.standard.entities.Human;
import rescuecore2.worldmodel.EntityID;
import rioneviewer3.Rione_ClassURN.classURN;

public class Rione_Exporter {
	
	private int time;
	private String directryPath;

	/********************************************************************************************
	 * 
	 * コンストラクター
	 * 
	 */
	public Rione_Exporter() {
		this.directryPath=Rione_Utility.getDataPath();
	}
	
	/********************************************************************************************
	 * 
	 * 必ずUpdateInfoで呼び出すこと
	 * 
	 * @param agentInfo
	 */
	private void checkTime(AgentInfo agentInfo){
		this.time=agentInfo.getTime()-1;
	}
	
	/********************************************************************************************
	 * 
	 * 全サイクル共通のログデータ(エリア系)を排出する
	 * 
	 * @param area
	 * @param color
	 * @throws Exception
	 */
	public void exportStaticData_Area(Area area, Color color) throws Exception{
		File file=new File(Rione_Utility.createPath(directryPath, "UserData", "static", "bin"));
		file.createNewFile();
		Rione_BinaryWriter writer=new Rione_BinaryWriter(file, true);
				
		writer.write(classURN.Area.getID());
		writer.write(getID(area.getID()));
		writer.write(color.getRed());
		writer.write(color.getGreen());
		writer.write(color.getBlue());
		writer.write(color.getOpacity());
		
	}
	
	/********************************************************************************************
	 * 
	 * 各サイクルごとのログデータ(エリア系)を排出する
	 * 
	 * @param area
	 * @param color
	 * @throws Exception
	 */
	public void exportData_Area(Area area, Color color) throws Exception{
		File file=new File(Rione_Utility.createPath(directryPath, "UserData", String.valueOf(this.time), "bin"));
		file.createNewFile();
		Rione_BinaryWriter writer=new Rione_BinaryWriter(file, true);
				
		writer.write(classURN.Area.getID());
		writer.write(getID(area.getID()));
		writer.write(color.getRed());
		writer.write(color.getGreen());
		writer.write(color.getBlue());
		writer.write(color.getOpacity());
		
	}
	
	/********************************************************************************************
	 * 
	 * 全サイクル共通のログデータ(エージェント系)を排出する
	 * 
	 * @param human
	 * @param color
	 * @throws Exception
	 */
	public void exportStaticData_Human(Human human, Color color) throws Exception{
		File file=new File(Rione_Utility.createPath(directryPath, "UserData", "static", "bin"));
		file.createNewFile();
		Rione_BinaryWriter writer=new Rione_BinaryWriter(file, true);
		
		writer.write(classURN.Human.getID());
		writer.write(getID(human.getID()));
		writer.write(color.getRed());
		writer.write(color.getGreen());
		writer.write(color.getBlue());
		writer.write(color.getOpacity());
		
	}
	
	/********************************************************************************************
	 * 
	 * 各サイクルごとのログデータ(エージェント系)を排出する
	 * 
	 * @param blockade
	 * @param color
	 * @throws Exception
	 */
	public void exportData_Human(Human human, Color color) throws Exception{
		File file=new File(Rione_Utility.createPath(directryPath, "UserData", String.valueOf(this.time), "bin"));
		file.createNewFile();
		Rione_BinaryWriter writer=new Rione_BinaryWriter(file, true);
		
		writer.write(classURN.Human.getID());
		writer.write(getID(human.getID()));
		writer.write(color.getRed());
		writer.write(color.getGreen());
		writer.write(color.getBlue());
		writer.write(color.getOpacity());
		
	}
	
	/********************************************************************************************
	 * 
	 * 全サイクル共通のログデータ(瓦礫)を排出する
	 * 
	 * @param blockade
	 * @param color
	 * @throws Exception
	 */
	public void exportStaticData_Blockade(Blockade blockade, Color color) throws Exception{
		File file=new File(Rione_Utility.createPath(directryPath, "UserData", "static", "bin"));
		file.createNewFile();
		Rione_BinaryWriter writer=new Rione_BinaryWriter(file, true);
		
		writer.write(classURN.Blockade.getID());
		writer.write(getID(blockade.getID()));
		writer.write(color.getRed());
		writer.write(color.getGreen());
		writer.write(color.getBlue());
		writer.write(color.getOpacity());
		
	}
	
	/********************************************************************************************
	 * 
	 * 各サイクルごとのログデータ(瓦礫系)を排出する
	 * 
	 * @param human
	 * @param color
	 * @throws Exception
	 */
	public void exportData_Blockade(Blockade blockade, Color color) throws Exception{
		File file=new File(Rione_Utility.createPath(directryPath, "UserData", String.valueOf(this.time), "bin"));
		file.createNewFile();
		Rione_BinaryWriter writer=new Rione_BinaryWriter(file, true);
		
		writer.write(classURN.Blockade.getID());
		writer.write(getID(blockade.getID()));
		writer.write(color.getRed());
		writer.write(color.getGreen());
		writer.write(color.getBlue());
		writer.write(color.getOpacity());
		
	}
	
	private int getID(EntityID id) {
		if(id!=null) {
			return id.getValue();
		}else {
			return -1;
		}
	}
	
	
	
	
	
	
	
	
	
}