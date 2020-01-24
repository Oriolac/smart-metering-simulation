package udl.cig.sms.connection.datagram;

import cat.udl.cig.fields.GroupElement;
import udl.cig.sms.connection.SMSDatagram;

public class GroupElementDatagram implements SMSDatagram {

    private final GroupElement element;

    public GroupElementDatagram(GroupElement element) {
        this.element = element;
    }

    public GroupElement getElement() {
        return element;
    }

    @Override
    public byte[] toByteArray() {
        return new byte[0];
    }
}
