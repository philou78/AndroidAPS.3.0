package info.nightscout.androidaps.plugins.pump.omnipod.comm;

import android.content.Context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import info.nightscout.androidaps.data.Profile;
import info.nightscout.androidaps.logging.L;
import info.nightscout.androidaps.plugins.pump.common.data.TempBasalPair;
import info.nightscout.androidaps.plugins.pump.common.hw.rileylink.RileyLinkCommunicationManager;
import info.nightscout.androidaps.plugins.pump.common.hw.rileylink.RileyLinkConst;
import info.nightscout.androidaps.plugins.pump.common.hw.rileylink.ble.RFSpy;
import info.nightscout.androidaps.plugins.pump.common.hw.rileylink.ble.data.RLMessage;
import info.nightscout.androidaps.plugins.pump.common.hw.rileylink.ble.defs.RLMessageType;
import info.nightscout.androidaps.plugins.pump.omnipod.OmnipodManager;
import info.nightscout.androidaps.plugins.pump.omnipod.comm.action.PairAction;
import info.nightscout.androidaps.plugins.pump.omnipod.comm.data.PodCommResponse;
import info.nightscout.androidaps.plugins.pump.omnipod.defs.state.PodSessionState;
import info.nightscout.androidaps.plugins.pump.omnipod.util.OmnipodUtil;
import info.nightscout.androidaps.utils.SP;

/**
 * Created by andy on 4.8.2019
 */
public class OmnipodCommunicationManager extends RileyLinkCommunicationManager implements OmnipodCommunicationManagerInterface {

    private static final Logger LOG = LoggerFactory.getLogger(L.PUMPCOMM);

    private static OmnipodCommunicationManager omnipodCommunicationManager;
    String errorMessage;
    OmnipodCommunicationService communicationService;
    OmnipodManager omnipodManager;


    public OmnipodCommunicationManager(Context context, RFSpy rfspy) {
        super(rfspy);
        omnipodCommunicationManager = this;
        OmnipodUtil.getPumpStatus().previousConnection = SP.getLong(
                RileyLinkConst.Prefs.LastGoodDeviceCommunicationTime, 0L);
        communicationService = new OmnipodCommunicationService(this);
        omnipodManager = new OmnipodManager(communicationService, getPodSessionState());
    }


    private PodSessionState getPodSessionState() {
        return null;
    }



    public static OmnipodCommunicationManager getInstance() {
        return omnipodCommunicationManager;
    }


    @Override
    protected void configurePumpSpecificSettings() {
        pumpStatus = OmnipodUtil.getPumpStatus();
    }


    @Override
    public <E extends RLMessage> E createResponseMessage(byte[] payload, Class<E> clazz) {
        // TODO

        //PumpMessage pumpMessage = new PumpMessage(payload);
        //eturn (E) pumpMessage;
        return null;
    }


    @Override
    public boolean tryToConnectToDevice() {
        return false; //isDeviceReachable(true);
    }


    public String getErrorResponse() {
        return this.errorMessage;
    }


    @Override
    public byte[] createPumpMessageContent(RLMessageType type) {
        return new byte[0];
    }


    private boolean isLogEnabled() {
        return L.isEnabled(L.PUMPCOMM);
    }


    // This are just skeleton methods, we need to see what we can get returned and act accordingly

    public PodCommResponse initPod() {
        omnipodManager.pairAndPrime();



        return null;
    }


    public PodCommResponse getPodStatus() {
        return null;
    }


    public PodCommResponse deactivatePod() {
        return null;
    }

    public PodCommResponse setBasalProfile(Profile profile) {
        return null;
    }

    public PodCommResponse resetPodStatus() {
        return null;
    }

    public PodCommResponse setBolus(Double parameter) {
        return null;
    }

    public PodCommResponse cancelBolus() {
        return null;
    }

    public PodCommResponse setTemporaryBasal(TempBasalPair tbr) {
        return null;
    }

    public PodCommResponse cancelTemporaryBasal() {
        return null;
    }

}
