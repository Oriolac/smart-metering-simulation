package udl.cig.sms.connection.datagram;

import cat.udl.cig.fields.GroupElement;
import udl.cig.sms.connection.SMSDatagram;

public class GroupElementDatagram implements SMSDatagram {

    public GroupElementDatagram(GroupElement element) {

    }

    @Override
    public byte[] toByteArray() {
        return new byte[0];
    }
}
