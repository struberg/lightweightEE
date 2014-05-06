package de.jaxenter.eesummit.caroline.utils;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Specializes;
import javax.interceptor.InvocationContext;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import java.util.Arrays;

import org.apache.deltaspike.jpa.impl.transaction.ResourceLocalTransactionStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author <a href="mailto:struberg@yahoo.de">Mark Struberg</a> 
 */
@Dependent
@Specializes
public class CaroLinePersistenceStrategy extends ResourceLocalTransactionStrategy
{
    /** 1 ms  in nanoTime ticks */
    final static long LONG_MILLISECOND = 1000000L;

    /** report all service calls which took longer than 200 ms */
    final static long LONG_RUNNING_THRESHOLD = 200L * LONG_MILLISECOND;

    private static Logger logger = LoggerFactory.getLogger(CaroLinePersistenceStrategy.class);

    /**
     * We override the execute to measure the time and log slow services
     */
    @Override
    public Object execute(InvocationContext invocationContext) throws Exception
    {
        long startTime = System.nanoTime();
        try
        {
            return super.execute(invocationContext);
        }
        finally
        {
            long stopTime = System.nanoTime();
            long elapsedTime = stopTime - startTime;
            if (elapsedTime > LONG_RUNNING_THRESHOLD)
            {
                logger.info("SLOW_SERVICE_DETECTED time = " + elapsedTime / LONG_MILLISECOND +
                        "ms method=" + invocationContext.getMethod() +
                        " parms=" + Arrays.toString(invocationContext.getParameters()));
            }

        }
    }

    /**
     * <p>Default CVE are really a shame for a otherwise very well written spec.
     * The default output is:
     * <pre>
     * javax.validation.ConstraintViolationException: A validation constraint failure occurred for class "de.jaxenter.eesummit.caroline.backend.entities.Customer".
     * </pre>
     * </p>
     *
     * <p>
     *     We will now make something useful out of it, by unpacking the causes and
     *     write them into the exception message:
     * </p>
     * <pre>
     *     javax.validation.ConstraintViolationException: A validation constraint failure occurred for class
     *        "de.jaxenter.eesummit.caroline.backend.entities.Customer".
     *     loginHash -> darf nicht "null" sein
    *      loginId -> darf nicht "null" sein
     * </pre>
     */
    @Override
    protected Exception prepareException(Exception e)
    {
        // make ConstraintViolationException message more meaningful
        if (e instanceof ConstraintViolationException)
        {
            ConstraintViolationException oldCvE = (ConstraintViolationException) e;
            StringBuilder msg = new StringBuilder(oldCvE.getMessage());
            msg.append("\n");

            if (oldCvE.getConstraintViolations() != null)
            {
                for (ConstraintViolation cv : oldCvE.getConstraintViolations())
                {
                    msg.append(cv.getPropertyPath());
                    msg.append(" -> ");
                    msg.append(cv.getMessage());
                    msg.append("\n");
                }
            }
            ConstraintViolationException newCvE = new ConstraintViolationException(msg.toString(), oldCvE.getConstraintViolations());
            newCvE.setStackTrace(oldCvE.getStackTrace());
            return newCvE;
        }

        return e;
    }

}
