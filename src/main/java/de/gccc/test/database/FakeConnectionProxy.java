package de.gccc.test.database;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.*;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("unchecked")
public class FakeConnectionProxy<T> implements InvocationHandler, ProxyImplHelper {
    final T target;
    //    final Savepoint sp;
    String sql = null;

    List<String> notIn = Arrays.asList(
            "commit",
            "setAutoCommit",
            "releaseSavepoint",
            "setSavepoint",
            "rollback",
            "setTransactionIsolation",
            "close",
            "abort"
    );

    public static Connection wrap(Connection connection) throws SQLException {
//        try {
        return (Connection) Proxy.newProxyInstance(
                connection.getClass().getClassLoader(),
                new Class[]{Connection.class},
                new FakeConnectionProxy<>(connection)
        );
//        } catch (SQLException se) {
//            System.out.println("Failed Creating a Connection Proxy: (" + se.getMessage() + ")");
//            throw new RuntimeException(se);
//        }
    }


    private FakeConnectionProxy(T target) throws SQLException {
        this(target, null);
    }

    private FakeConnectionProxy(T target, String sql) throws SQLException {
        this.target = target;
        this.sql = sql;
//        this.sp = ((Connection) target).setSavepoint("CC_" + ProxyImplHelper.uuid());
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object r;
        if (method.getName().equals("unwrap") && args.length == 1) {
            r = unwrap((Class) args[0]);
        } else if (notIn.contains(method.getName())) {
            // if any method name that is contained in notIn, should actually just return null and have a Void result
            r = null;
        } else {
            r = method.invoke(target, args);
        }

        if (method.getName().equals("prepareCall") || method.getName().equals("prepareStatement")) {
            r = wrap(r, (String) args[0]);
        } else if (r == null) {
            r = Void.TYPE;
        } else {
            r = wrap(r, null);
        }

        return r;
    }


    private <V> V unwrap(Class<V> iface) throws SQLException {
        return iface.cast(target);
    }

    private Object wrap(Object r, String sql) throws Exception {
        if (r instanceof Connection) {
            return wrapByGenericProxy(r, Connection.class, sql);
        }
        return r;
    }

    static Object wrapByGenericProxy(Object r, Class interf, String sql) throws SQLException {
        return Proxy.newProxyInstance(r.getClass().getClassLoader(), new Class[]{interf}, new FakeConnectionProxy(r, sql));
    }

}
