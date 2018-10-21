package rioneviewer3;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;


import adf.agent.action.Action;
import adf.agent.action.ambulance.ActionLoad;
import adf.agent.action.ambulance.ActionRescue;
import adf.agent.action.ambulance.ActionUnload;
import adf.agent.action.common.ActionMove;
import adf.agent.action.common.ActionRest;
import adf.agent.action.fire.ActionExtinguish;
import adf.agent.action.fire.ActionRefill;
import adf.agent.action.police.ActionClear;
import adf.agent.communication.MessageManager;
import adf.agent.communication.standard.bundle.centralized.CommandAmbulance;
import adf.agent.communication.standard.bundle.centralized.CommandFire;
import adf.agent.communication.standard.bundle.centralized.CommandPolice;
import adf.agent.communication.standard.bundle.information.MessageAmbulanceTeam;
import adf.agent.communication.standard.bundle.information.MessageBuilding;
import adf.agent.communication.standard.bundle.information.MessageCivilian;
import adf.agent.communication.standard.bundle.information.MessageFireBrigade;
import adf.agent.communication.standard.bundle.information.MessagePoliceForce;
import adf.agent.communication.standard.bundle.information.MessageRoad;
import adf.agent.info.AgentInfo;
import adf.agent.info.ScenarioInfo;
import adf.agent.info.WorldInfo;
import adf.component.communication.CommunicationMessage;
import rescuecore2.config.Config;
import rescuecore2.config.NoSuchConfigOptionException;
import rescuecore2.standard.entities.AmbulanceCentre;
import rescuecore2.standard.entities.FireStation;
import rescuecore2.standard.entities.GasStation;
import rescuecore2.standard.entities.Human;
import rescuecore2.standard.entities.PoliceOffice;
import rescuecore2.standard.entities.Refuge;
import rescuecore2.standard.entities.StandardEntity;
import rescuecore2.standard.entities.StandardEntityURN;
import rescuecore2.standard.kernel.comms.ChannelCommunicationModel;
import rescuecore2.worldmodel.EntityID;
import rioneviewer3.Rione_ClassURN.actionURN;
import rioneviewer3.Rione_ClassURN.messageURN;




public class Rione_Exporter_Source{

	private int time=0;
	private String directryPath;
	private boolean isSetup=false;
	
	public Rione_Exporter_Source() {
		this.directryPath=Rione_Utility.getDataPath();
	}

	private void checkTime(AgentInfo agentInfo){
		/*while(Rione_Utility.isTimeExist(Rione_Utility.createPath(directryPath, "Time", String.valueOf(time+1), "bin"))) {
			time++;
		}
		if(this.time+1!=agentInfo.getTime()) {
			Rione_Utility.debugError("エージェント["+agentInfo.getID()+"] の内部時刻がずれた");
		}*/
		this.time=agentInfo.getTime()-1;
	}

	public void exportAgentInfo(final WorldInfo worldInfo,final AgentInfo agentInfo,final MessageManager messageManager,final Action action,final EntityID targetEntity){
		try {
			if(!isSetup) {
				Rione_Utility.createDirectory(directryPath, "Source", String.valueOf(getID(agentInfo.getID())));
				isSetup=true;
			}
			checkTime(agentInfo);
			exportActionInfo(worldInfo, agentInfo, messageManager, action, targetEntity);
			exportMessageInfo(worldInfo, agentInfo, messageManager);
		} catch (Exception e) {
			e.printStackTrace();
		}
		//exportMessageInfo(worldInfo, agentInfo, messageManager);
	}
	
	private void exportActionInfo(WorldInfo worldInfo,AgentInfo agentInfo,MessageManager messageManager,Action action,EntityID detectorTarget) throws Exception{
		File file=Rione_Utility.createFile(directryPath, "Source/"+getID(agentInfo.getID()), "ac"+this.time, "bin");
		Rione_BinaryWriter writer=new Rione_BinaryWriter(file, true);
		writer.write(getID(agentInfo.getID()));
		actionURN actionType=getAction(action);
		writer.write(actionType.getID());
		writer.write(getID(detectorTarget));
		writer.write(getID(getSomeoneOnBoard(agentInfo)));
		writer.write(agentInfo.getThinkTimeMillis());
		writer.write(toArray(agentInfo.getChanged().getChangedEntities()));
		switch (actionType) {
		case ActionClear:
			ActionClear clear=(ActionClear)action;
			writer.write(getID(clear.getTarget()));
			writer.write(clear.getUseOldFunction());
			writer.write(clear.getPosX());
			writer.write(clear.getPosY());
			break;
		case ActionExtinguish:
			ActionExtinguish extinguish=(ActionExtinguish)action;
			writer.write(getID(extinguish.getTarget()));
			writer.write(extinguish.getPower());
			break;
		case ActionLoad:
			ActionLoad load=(ActionLoad)action;
			writer.write(getID(load.getTarget()));
			break;
		case ActionMove:
			ActionMove move=(ActionMove)action;
			writer.write(toArray(move.getPath()));
			writer.write(move.getUsePosition());
			writer.write(move.getPosX());
			writer.write(move.getPosY());
			break;
		case ActionRefill:
			break;
		case ActionRescue:
			ActionRescue rescue=(ActionRescue)action;
			writer.write(getID(rescue.getTarget()));
			break;
		case ActionRest:
			break;
		case ActionSearch:
			break;
		case ActionUnload:
			break;
		}
		writer.close();
	}

	public void exportMessageInfo(WorldInfo worldInfo,AgentInfo agentInfo,MessageManager messageManager) throws Exception{
		File file=Rione_Utility.createFile(directryPath, "Source/"+getID(agentInfo.getID()), "me"+this.time, "bin");
		Rione_BinaryWriter writer=new Rione_BinaryWriter(file);
		writer.write(getID(agentInfo.getID()));
		writeMessage(messageManager.getReceivedMessageList(), writer);
		writeMessage(messageManager.getSendMessageList(), writer);
		writer.close();
	}

	private void writeMessage(List<CommunicationMessage> list, Rione_BinaryWriter writer) throws Exception{
		if(list!=null&&!list.isEmpty()){
			writer.write(list.size());
			for(CommunicationMessage communicationMessage:list){
				messageURN messageType=getMessageURN(communicationMessage);
				writer.write(messageType.getID());
				switch (messageType) {
				case CommandAmbulance:
					CommandAmbulance messageCA=(CommandAmbulance)communicationMessage;
					writer.write(getID(messageCA.getSenderID()));
					writer.write(getID(messageCA.getTargetID()));
					writer.write(getID(messageCA.getToID()));
					writer.write(messageCA.getAction());
					writer.write(communicationMessage.isRadio());
					writer.write(calcMessageSize(messageCA.toByteArray()));
					break;
				case CommandFire:
					CommandFire messageCF=(CommandFire)communicationMessage;
					writer.write(getID(messageCF.getSenderID()));
					writer.write(getID(messageCF.getTargetID()));
					writer.write(getID(messageCF.getToID()));
					writer.write(messageCF.getAction());
					writer.write(communicationMessage.isRadio());
					writer.write(calcMessageSize(messageCF.toByteArray()));
					break;
				case CommandPolice:
					CommandPolice messageCP=(CommandPolice)communicationMessage;
					writer.write(getID(messageCP.getSenderID()));
					writer.write(getID(messageCP.getTargetID()));
					writer.write(getID(messageCP.getToID()));
					writer.write(messageCP.getAction());
					writer.write(communicationMessage.isRadio());
					writer.write(calcMessageSize(messageCP.toByteArray()));
					break;
				case MessageAmbulanceTeam:
					MessageAmbulanceTeam messageAT=(MessageAmbulanceTeam)communicationMessage;
					writer.write(getID(messageAT.getSenderID()));
					writer.write(getID(messageAT.getAgentID()));
					writer.write(messageAT.getAction());
					writer.write(messageAT.getBuriedness());
					writer.write(messageAT.getDamage());
					writer.write(messageAT.getHP());
					writer.write(getID(messageAT.getPosition()));
					writer.write(getID(messageAT.getTargetID()));
					writer.write(communicationMessage.isRadio());
					writer.write(calcMessageSize(messageAT.toByteArray()));
					break;
				case MessageBuilding:
					MessageBuilding messageBu=(MessageBuilding)communicationMessage;
					writer.write(getID(messageBu.getSenderID()));
					writer.write(getID(messageBu.getBuildingID()));
					writer.write(messageBu.getBrokenness());
					writer.write(messageBu.getFieryness());
					writer.write(messageBu.getTemperature());
					writer.write(communicationMessage.isRadio());
					writer.write(calcMessageSize(messageBu.toByteArray()));
					break;
				case MessageCivilian:
					MessageCivilian messageCiv=(MessageCivilian)communicationMessage;
					writer.write(getID(messageCiv.getSenderID()));
					writer.write(getID(messageCiv.getAgentID()));
					writer.write(messageCiv.getBuriedness());
					writer.write(messageCiv.getDamage());
					writer.write(messageCiv.getHP());
					writer.write(getID(messageCiv.getPosition()));
					writer.write(communicationMessage.isRadio());
					writer.write(calcMessageSize(messageCiv.toByteArray()));
					break;
				case MessageFireBrigade:
					MessageFireBrigade messageFB=(MessageFireBrigade)communicationMessage;
					writer.write(getID(messageFB.getSenderID()));
					writer.write(getID(messageFB.getAgentID()));
					writer.write(messageFB.getAction());
					writer.write(messageFB.getBuriedness());
					writer.write(messageFB.getDamage());
					writer.write(messageFB.getHP());
					writer.write(getID(messageFB.getPosition()));
					writer.write(getID(messageFB.getTargetID()));
					writer.write(communicationMessage.isRadio());
					writer.write(calcMessageSize(messageFB.toByteArray()));
					break;
				case MessagePoliceForce:
					MessagePoliceForce messagePF=(MessagePoliceForce)communicationMessage;
					writer.write(getID(messagePF.getSenderID()));
					writer.write(getID(messagePF.getAgentID()));
					writer.write(messagePF.getAction());
					writer.write(messagePF.getBuriedness());
					writer.write(messagePF.getDamage());
					writer.write(messagePF.getHP());
					writer.write(getID(messagePF.getPosition()));
					writer.write(getID(messagePF.getTargetID()));
					writer.write(communicationMessage.isRadio());
					writer.write(calcMessageSize(messagePF.toByteArray()));
					break;
				case MessageRoad:
					MessageRoad messageRo=(MessageRoad)communicationMessage;
					writer.write(getID(messageRo.getSenderID()));
					writer.write(getID(messageRo.getBlockadeID()));
					if(getID(messageRo.getBlockadeID())!=-1) {
						writer.write(messageRo.getBlockadeX());
						writer.write(messageRo.getBlockadeY());
					}
					writer.write(getID(messageRo.getRoadID()));
					writer.write(messageRo.getRepairCost());
					writer.write(communicationMessage.isRadio());
					writer.write(calcMessageSize(messageRo.toByteArray()));
					break;
				}
				writer.flush();
			}
		}else {
			writer.write(0);
		}
	}

	private messageURN getMessageURN(CommunicationMessage communicationMessage) {
		if(communicationMessage instanceof MessageAmbulanceTeam){
			return messageURN.MessageAmbulanceTeam;
		}else if(communicationMessage instanceof MessageFireBrigade){
			return messageURN.MessageFireBrigade;
		}else if(communicationMessage instanceof MessagePoliceForce){
			return messageURN.MessagePoliceForce;
		}else if(communicationMessage instanceof MessageCivilian){
			return messageURN.MessageCivilian;
		}else if(communicationMessage instanceof MessageBuilding){
			return messageURN.MessageBuilding;
		}else if(communicationMessage instanceof MessageRoad){
			return messageURN.MessageRoad;
		}else if(communicationMessage instanceof CommandAmbulance){
			return messageURN.CommandAmbulance;
		}else if(communicationMessage instanceof CommandFire){
			return messageURN.CommandFire;
		}else if(communicationMessage instanceof CommandPolice){
			return messageURN.CommandPolice;
		}
		return null;
	}

	public void calcResume(WorldInfo worldInfo,ScenarioInfo scenarioInfo) {
		try {
			java.io.File scenarioData=new java.io.File(directryPath+"World/ScenarioData");
			if (scenarioData.createNewFile()){
				FileWriter fileWriter=new FileWriter(scenarioData);
				int clearDistance=scenarioInfo.getClearRepairDistance();
				int clearRad=scenarioInfo.getClearRepairRad();
				int clearRate=scenarioInfo.getClearRepairRate();
				int channelCount=scenarioInfo.getCommsChannelsCount();
				int channelMaxOffice=scenarioInfo.getCommsChannelsMaxOffice();
				int channelMaxPlatoon=scenarioInfo.getCommsChannelsMaxPlatoon();
				int extinguishMaxDistance=scenarioInfo.getFireExtinguishMaxDistance();
				int extinguishMaxSum=scenarioInfo.getFireExtinguishMaxSum();
				int refillHydrantRate=scenarioInfo.getFireTankRefillHydrantRate();
				int refillRefugeRate=scenarioInfo.getFireTankRefillRate();
				int connectTime=scenarioInfo.getKernelStartupConnectTime();
				//int timeSteps=scenarioInfo.getKernelTimesteps();//値が存在しない
				int perceptionLosMaxDistance=scenarioInfo.getPerceptionLosMaxDistance();
				int perceptionLosDamage=scenarioInfo.getPerceptionLosPrecisionDamage();
				int perceptionLosHP=scenarioInfo.getPerceptionLosPrecisionHp();
				int ac=scenarioInfo.getScenarioAgentsAc();
				int at=scenarioInfo.getScenarioAgentsAt();
				int fb=scenarioInfo.getScenarioAgentsFb();
				int fs=scenarioInfo.getScenarioAgentsFs();
				int pf=scenarioInfo.getScenarioAgentsPf();
				int po=scenarioInfo.getScenarioAgentsPo();
				int voiceSize=scenarioInfo.getVoiceMessagesSize();
				int sizeSX=worldInfo.getWorldBounds().first().first().intValue();
				int sizeSY=worldInfo.getWorldBounds().first().second().intValue();
				int sizeEX=worldInfo.getWorldBounds().second().first().intValue();
				int sizeEY=worldInfo.getWorldBounds().second().second().intValue();
				Config config=scenarioInfo.getRawConfig();
				int bandwidth=0;
				String isRadio="true";
				try {bandwidth=config.getIntValue(ChannelCommunicationModel.PREFIX+1+".bandwidth");}
				catch (NoSuchConfigOptionException e) {System.out.println("Error : 帯域幅が存在しません");isRadio="false";}
				String line=new String();
				line+="ClearDistance:"+clearDistance+"\n";
				line+="ClearRad:"+clearRad+"\n";
				line+="ClearRate:"+clearRate+"\n";
				line+="ChannelCount:"+channelCount+"\n";
				line+="ChannelMaxOffice:"+channelMaxOffice+"\n";
				line+="ChannelMaxPlatoon:"+channelMaxPlatoon+"\n";
				line+="ExtinguishMaxDistance:"+extinguishMaxDistance+"\n";
				line+="ExtinguishMaxSum:"+extinguishMaxSum+"\n";
				line+="RefillHydrantRate:"+refillHydrantRate+"\n";
				line+="RefillRefugeRate:"+refillRefugeRate+"\n";
				line+="ConnectTime:"+connectTime+"\n";
				line+="PerceptionLosMaxDistance:"+perceptionLosMaxDistance+"\n";
				line+="PerceptionLosDamage:"+perceptionLosDamage+"\n";
				line+="PerceptionLosHP:"+perceptionLosHP+"\n";
				line+="Ac:"+ac+"\n";
				line+="At:"+at+"\n";
				line+="Fb:"+fb+"\n";
				line+="Fs:"+fs+"\n";
				line+="Pf:"+pf+"\n";
				line+="Po:"+po+"\n";
				line+="VoiceSize:"+voiceSize+"\n";
				line+="WorldBoundsSX:"+sizeSX+"\n";
				line+="WorldBoundsSY:"+sizeSY+"\n";
				line+="WorldBoundsEX:"+sizeEX+"\n";
				line+="WorldBoundsEY:"+sizeEY+"\n";
				line+="Bandwidth:"+bandwidth+"\n";
				line+="Radio:"+isRadio+"\n";
				fileWriter.write(line);
				fileWriter.close();
			}
		}catch (IOException e) {e.printStackTrace();}
	}

	/**************************************************************************************************************************/

	private actionURN getAction(Action action) {
		if (action==null) {
			return actionURN.ActionSearch;
		}else if (action instanceof ActionMove) {
			return actionURN.ActionMove;
		}else if (action instanceof ActionRest) {
			return actionURN.ActionRest;
		}else if (action instanceof ActionRescue) {
			return actionURN.ActionRescue;
		}else if (action instanceof ActionLoad) {
			return actionURN.ActionLoad;
		}else if (action instanceof ActionUnload) {
			return actionURN.ActionUnload;
		}else if (action instanceof ActionExtinguish) {
			return actionURN.ActionExtinguish;
		}else if (action instanceof ActionRefill) {
			return actionURN.ActionRefill;
		}else if (action instanceof ActionClear) {
			return actionURN.ActionClear;
		}
		return actionURN.ActionSearch;
	}

	private int[] toArray(List<EntityID> list) {
		List<EntityID> result=new ArrayList<>();
		if(list!=null&&!list.isEmpty()) {
			for(EntityID id: list) {
				if(id!=null) {
					result.add(id);
				}
			}
			int[] array=new int[result.size()];
			for(int i=0;i<array.length;i++) {
				array[i]=result.get(i).getValue();
			}
			if(list.size()!=result.size()) {
				Rione_Utility.debugError("パスプラのリストに null が入っている");
			}
			return array;
		}else {
			return new int[0];
		}
	}
	
	private int calcMessageSize(byte[] array) {
		if(array!=null) {
			return array.length;
		}
		return 0;
	}

	private int[] toArray(Set<EntityID> list) {
		List<EntityID> result=new ArrayList<>();
		if(list!=null&&!list.isEmpty()) {
			for(EntityID id: list) {
				if(id!=null) {
					result.add(id);
				}
			}
			int[] array=new int[result.size()];
			for(int i=0;i<array.length;i++) {
				array[i]=result.get(i).getValue();
			}
			return array;
		}else {
			return new int[0];
		}
	}

	private EntityID getSomeoneOnBoard(AgentInfo agentInfo) {
		Human human=agentInfo.someoneOnBoard();
		if(human!=null) {
			return human.getID();
		}else {
			return null;
		}
	}

	private int getID(EntityID id) {
		if(id!=null) {
			return id.getValue();
		}else {
			return -1;
		}
	}

}