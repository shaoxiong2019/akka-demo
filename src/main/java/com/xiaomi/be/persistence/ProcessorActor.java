package com.xiaomi.be.persistence;

import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.Procedure;
import akka.persistence.*;

import java.io.Serializable;
import java.util.UUID;

public class ProcessorActor extends UntypedPersistentActor {
    LoggingAdapter LOG = Logging.getLogger(getContext().system(), this);

    private ProcessorState state = new ProcessorState();

    private int getEvents() {
        return state.size();
    }

    @Override
    /**
     * 禁用启动时的自动恢复 onReceiveRecover
     */
    public void preStart() throws Exception {
    }

    @Override
    public void onReceiveRecover(Object msg) throws Exception {
//        Recover.create(0);
        if (msg instanceof RecoveryCompleted) {
            // todo
        } else if (msg instanceof Evt) {
            state.update((Evt) msg);
        } else if (msg instanceof SnapshotOffer) {
            state = (ProcessorState) ((SnapshotOffer) msg).snapshot();
        } else {
            unhandled(msg);
        }
    }

    /**
     * cmd - 包含事件信息的持久化消息
     */
    @Override
    public void onReceiveCommand(final Object msg) throws Exception {
        if (msg instanceof Cmd) {
            String data = ((Cmd) msg).data;
            final Evt evt = new Evt(data, UUID.randomUUID().toString());
            persist(evt, new Procedure<Evt>() {
                @Override
                public void apply(Evt evt) throws Exception {
                    state.update(evt);
                    // broadcast event on eventstream 发布该事件
                    getContext().system().eventStream().publish(evt);
                }
            });

        } else if (msg.equals("snapshot")) {
            saveSnapshot(state.copy());
        } else if (msg.equals("printstate")) {
            LOG.info(state.toString());
        } else {
            unhandled(msg);
        }
    }


//    @Override
//    public void onReceiveCommand1(final Object msg) throws Exception {
//        sender().tell(msg, self());
//        if (msg instanceof Cmd) {
//            persistAsync(String.format("evt-%s-1", msg), new Procedure<String>() {
//                @Override
//                public void apply(String evt) throws Exception {
//                    sender().tell(evt, self());
//                }
//            });
//
//            persistAsync(String.format("evt-%s-2", msg), new Procedure<String>() {
//                @Override
//                public void apply(String evt) throws Exception {
//                    sender().tell(evt, self());
//                }
//            });
//        } else if (msg.equals("snap")) {
//            saveSnapshot(state.copy());
//        } else {
//            unhandled(msg);
//        }
//    }

    @Override
    public String persistenceId() {
        return "persist-uniq-id";
    }

    public static class Cmd implements Serializable {
        public String data;

        public Cmd(String data) {
            this.data = data;
        }
    }

    public static class Evt implements Serializable {
        public String data;
        public String uuid;

        public Evt(String data, String uuid) {
            this.data = data;
            this.uuid = uuid;
        }
    }
}
