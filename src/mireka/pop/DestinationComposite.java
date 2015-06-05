package mireka.pop;

import mireka.destination.MailDestination;
import mireka.smtp.RejectExceptionExt;
import mireka.transmission.Mail;

import java.util.Arrays;

public class DestinationComposite implements MailDestination {

    private MailDestination[] destinations;

    public DestinationComposite(MailDestination... destinations) {
        this.destinations = destinations;
    }

    @Override
    public void data(Mail mail) throws RejectExceptionExt {
        for (MailDestination destination : destinations) {
            destination.data(mail);
        }
    }

    @Override
    public String toString() {
        return "DestinationComposite{" +
                "destinations=" + Arrays.toString(destinations) +
                '}';
    }
}
