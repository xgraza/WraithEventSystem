package biden.give.bombs.to.bomb.donetsk.children;

import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.util.function.Consumer;

/**
 * A class representing an event listener.
 */
public class Listener
{
    private final Object instance;
    private final Method method;
    private final EventListener listener;

    private final Consumer<Object> consumer;

    public Listener(Object instance, Method method, EventListener listener)
    {
        this.instance = instance;
        this.method = method;
        this.listener = listener;
        this.consumer = createConsumer();
    }

    @SuppressWarnings("unchecked")
    private Consumer<Object> createConsumer()
    {
        try
        {
            MethodHandles.Lookup lookup = MethodHandles.lookup();
            MethodType methodType = MethodType.methodType(void.class, method.getParameters()[0].getType());
            MethodHandle methodHandle = lookup.findVirtual(method.getDeclaringClass(), method.getName(), methodType);
            MethodType invokedType = MethodType.methodType(Consumer.class, method.getDeclaringClass());
            MethodHandle lambdaFactory = LambdaMetafactory.metafactory(
                    lookup, "accept", invokedType, MethodType.methodType(void.class, Object.class), methodHandle, methodType).getTarget();

            return (Consumer<Object>) lambdaFactory.invoke(instance);
        } catch (Throwable throwable)
        {
            throw new IllegalStateException(throwable.getMessage());
        }
    }

    /**
     * The invoke method accepting the Event.
     *
     * @param event represents the Event class.
     */
    public void invoke(Object event)
    {
        consumer.accept(event);
    }

    public int getPriority()
    {
        return listener.priority().getVal();
    }

    public boolean isReceiveCanceled()
    {
        return listener.receiveCanceled();
    }

    public Method getMethod()
    {
        return method;
    }

    public Object getInstance()
    {
        return instance;
    }

}