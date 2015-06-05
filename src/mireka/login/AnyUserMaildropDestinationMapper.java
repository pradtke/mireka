package mireka.login;

import mireka.address.GenericRecipient;
import mireka.address.LocalPart;
import mireka.address.Recipient;
import mireka.address.RemotePart;
import mireka.destination.Destination;
import mireka.destination.MailDestination;
import mireka.filter.local.table.RecipientDestinationMapper;
import mireka.forward.ForwardDestination;
import mireka.forward.Member;
import mireka.forward.Srs;
import mireka.pop.DestinationComposite;
import mireka.pop.MaildropDestination;
import mireka.pop.store.MaildropRepository;
import mireka.transmission.Transmitter;

import javax.inject.Inject;
import java.util.*;

/**
 * Map any recipient to a maildrop with the recipients names. Maildrop will automatically be created if
 * it doesn't exist
 */
public class AnyUserMaildropDestinationMapper implements RecipientDestinationMapper {

    /**
     * The transmitter which will be used to redistribute the incoming mail to
     * the members.
     */
    private Transmitter transmitter;

    private Srs srs;

    private MaildropRepository maildropRepository;

    private String ignoreSuffix = ".jive.mx";

    private List<String> jiveForwards = new ArrayList<>();

    @Override
    public Destination lookup(Recipient recipient) {

        List<MailDestination> destinations = new ArrayList<MailDestination>();
        LocalPart recipientLocalPart = recipient.localPart();
        // Username maildrop
        MaildropDestination userDestination = new MaildropDestination();
        userDestination.setMaildropName(recipientLocalPart.displayableName().toLowerCase(Locale.US));
        userDestination.setMaildropRepository(maildropRepository);
        destinations.add(userDestination);

        //Domain name maildrop
        if (recipient instanceof GenericRecipient) {
            RemotePart remotePart = ((GenericRecipient) recipient).getMailbox().getRemotePart();
            String domain = remotePart.smtpText().toLowerCase(Locale.US);
            if (ignoreSuffix != null) {
                int index = domain.lastIndexOf(ignoreSuffix);
                if (index > 0) {
                    domain = domain.substring(0, index);
                }
            }

            MaildropDestination domainDestination = new MaildropDestination();
            domainDestination.setMaildropName(domain);
            domainDestination.setMaildropRepository(maildropRepository);
            destinations.add(domainDestination);
        }

        addJiveForwarding(recipient, destinations);
        return new DestinationComposite(destinations.toArray(new MailDestination[0]));
    }

    private void addJiveForwarding(Recipient recipient, List<MailDestination> destinations) {
        String username = recipient.localPart().displayableName().toLowerCase(Locale.US);
        if (jiveForwards.contains(username)) {
            ForwardDestination forwardDestination = new ForwardDestination();
            forwardDestination.setSrs(srs);
            forwardDestination.setTransmitter(transmitter);
            List<Member> members = new ArrayList<>();
            Member member = new Member();
            member.setAddress(username + "@jivesoftware.com");
            members.add(member);
            forwardDestination.setMembers(members);
            destinations.add(forwardDestination);
        }
    }

    /**
     * @x.category GETSET
     */
    public void setMaildropRepository(MaildropRepository maildropRepository) {
        this.maildropRepository = maildropRepository;
    }

    /**
     * @x.category GETSET
     */
    public MaildropRepository getMaildropRepository() {
        return maildropRepository;
    }


    /**
     * @x.category GETSET
     */
    @Inject
    public void setTransmitter(Transmitter transmitter) {
        this.transmitter = transmitter;
    }

    /**
     * @x.category GETSET
     */
    @Inject
    public void setSrs(Srs srs) {
        this.srs = srs;
    }

    public String getIgnoreSuffix() {
        return ignoreSuffix;
    }

    public void setIgnoreSuffix(String ignoreSuffix) {
        this.ignoreSuffix = ignoreSuffix;
    }

    public List<String> getJiveForwards() {
        return jiveForwards;
    }

    public void setJiveForwards(List<String> jiveForwards) {
        //this.jiveForwards = new HashSet<>(jiveForwards);
        this.jiveForwards = jiveForwards;
    }
}
