package com.xiaomi.be;


import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.transactor.Coordinated;
import akka.util.Timeout;

import java.util.HashMap;

/***
 *  实现方式
 *  1. actor协作，通过actor.tell(coordinated.coordinate( msg ));
 *  2. UntypedTransactor
 *  3. Coordinating Typed Actors
 *
 *  Refer to url :
 *  1) http://doc.akka.io/docs/akka/1.3.1/java/transactors.html
 *  2) http://www.jdon.com/concurrent/akka1.html
 *  3) http://www.jdon.com/concurrent/akka/stm.html
 */
public class TransactorDemo {
    static HashMap<String, Integer> bankDB = new HashMap<>();
    static ActorSystem system;

    static {
        bankDB.put("Jack", 100);
        bankDB.put("David", 200);
    }

    static class Withdraw {
        int amount;

        public Withdraw(int amount) {
            this.amount = amount;
        }
    }

    static class Deposit {
        int amount;

        public Deposit(int amount) {
            this.amount = amount;
        }
    }

    static class Transfer {
        ActorRef from;
        ActorRef to;
        int amount;

        public Transfer(ActorRef from, ActorRef to, int amount) {
            this.from = from;
            this.to = to;
            this.amount = amount;
        }
    }

    static class PrintBalance {
        String account;
    }


    static class CoordinatedAccountActor extends UntypedActor {

        @Override
        public void onReceive(Object incoming) throws Exception {
            if (incoming instanceof Coordinated) {
                Coordinated coordinated = (Coordinated) incoming;
                Object message = coordinated.getMessage();
                if (message instanceof Transfer) {
                    final Transfer msg = (Transfer) message;
                    msg.to.tell(coordinated.coordinate(new Deposit(msg.amount)), ActorRef.noSender());

                    coordinated.atomic(new Runnable() {
                        @Override
                        public void run() {
                            msg.from.tell(new Withdraw(msg.amount), ActorRef.noSender());
                        }
                    });
                }
            } else {
                String account = getSelf().toString();
                if (incoming instanceof Withdraw) {
                    bankDB.put(account, bankDB.get(account) - ((Withdraw) incoming).amount);
                } else if (incoming instanceof Deposit) {
                    bankDB.put(account, bankDB.get(account) + ((Deposit) incoming).amount);
                } else {
                    unhandled(incoming);
                }
            }
        }
    }

//    static class CoordinatedAccountActor extends UntypedTransactor {
//
//        @Override
//        public Set<SendTo> coordinate(Object message) throws Exception {
//            if (message instanceof Transfer) {
//                Deposit deposit = new Deposit(((Transfer) message).to.toString(), ((Transfer) message).amount);
//                return include(((Transfer) message).to, deposit);
//            } else {
//                unhandled(message);
//                return null;
//            }
//        }
//
//        @Override
//        public void atomically(Object message) throws Exception {
//            if (message instanceof Withdraw) {
//                String account = ((Withdraw) message).ref.toString();
//                bankDB.put(account, bankDB.get(account) - ((Withdraw) message).amount);
//            } else if (message instanceof Deposit) {
//                Deposit _msg = (Deposit) message;
//                bankDB.put(_msg.account, bankDB.get(_msg.account) + _msg.amount);
//            } else {
//                unhandled(message);
//            }
//        }
//    }

    public static void main(String[] args) {
        ActorSystem system = ActorSystem.create("account-system");
        ActorRef davidRef = system.actorOf(Props.create(CoordinatedAccountActor.class), "david");

        ActorRef jackRef = system.actorOf(Props.create(CoordinatedAccountActor.class), "jack");
        Coordinated transfer = new Coordinated(new Transfer(davidRef, jackRef, 10), Timeout.longToTimeout(3000));

        davidRef.tell(transfer, ActorRef.noSender());
    }
}
