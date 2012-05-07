package ness.httpserver2.log.file;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import ness.httpserver2.HttpServerConfig;
import ness.httpserver2.log.LogFields;
import ness.httpserver2.log.LogFields.LogField;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.RequestLog;
import org.eclipse.jetty.server.Response;
import org.eclipse.jetty.util.component.AbstractLifeCycle;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Charsets;
import com.google.common.io.Closeables;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.nesscomputing.logging.Log;

/**
 * A simple non-rolling access log writer, which writes out to a tab separated file.
 * In general, this will be configured via a {@link HttpServerConfig}.
 */
@Singleton
public class FileRequestLog extends AbstractLifeCycle implements RequestLog
{
    private static final Log LOG = Log.findLog();

    private final List<String> logFields;
    private final Set<String> blackList;
    private final File requestLogFile;

    private final AtomicReference<PrintWriter> requestLogWriterHolder = new AtomicReference<PrintWriter>();
    private final Map<String, LogField> knownFields;

    @Inject
    public FileRequestLog(final FileRequestLogConfig requestLogConfig,
                          final Map<String, LogField> knownFields)
    {
        final List<String> logFields = requestLogConfig.getLogFields();
        LogFields.validateLogFields(knownFields, logFields);
        this.logFields = logFields;
        this.blackList = requestLogConfig.getBlacklist();
        this.requestLogFile = new File(requestLogConfig.getFileName());
        this.knownFields = knownFields;
    }

    @Override
    public void doStart()
    {
        if (requestLogFile.exists() && !requestLogFile.isFile()) {
            LOG.warn("Log file \"%s\" exists, but is not a file!", requestLogFile.getAbsolutePath());
            return;
        }

        final File logPath = requestLogFile.getParentFile();
        if (!logPath.mkdirs() && !logPath.exists()) {
            LOG.warn("Cannot create \"%s\" and path does not already exist!", logPath.getAbsolutePath());
        }

        LOG.info("Opening request log at \"%s\"", requestLogFile.getAbsolutePath());
        try {
            setWriter(new PrintWriter(new OutputStreamWriter(new FileOutputStream(requestLogFile, true), Charsets.UTF_8), true));

        } catch (FileNotFoundException e) {
            LOG.error(e, "Could not open request log \"%s\"", requestLogFile.getAbsolutePath());
        }
    }

    @VisibleForTesting
    void setWriter(final PrintWriter printWriter)
    {
        if (!requestLogWriterHolder.compareAndSet(null, printWriter)) {
            Closeables.closeQuietly(printWriter);
        }
    }

    @Override
    public void doStop()
    {
        final PrintWriter requestLogWriter = requestLogWriterHolder.getAndSet(null);
        if (requestLogWriter != null) {
            LOG.info("Closing request log \"%s\"", requestLogFile.getAbsolutePath());
            Closeables.closeQuietly(requestLogWriter);
        }
    }

    @Override
    public void log(final Request request, final Response response)
    {
        final String requestUri = request.getRequestURI();

        for (String blackListEntry : blackList) {
            if (StringUtils.startsWith(requestUri, blackListEntry)) {
                return;
            }
        }

        final PrintWriter requestLogWriter = requestLogWriterHolder.get();
        if (requestLogWriter != null) {
            synchronized (this) {
                for (Iterator<String> it = logFields.iterator(); it.hasNext(); ) {
                    // Parse out fields that have parameters e.g. header:X-Trumpet-Track, and print
                    String[] chunks = StringUtils.split(it.next(), ":");

                    final LogField field = knownFields.get(chunks[0]);
                    if (chunks.length == 1) {
                        requestLogWriter.print(ObjectUtils.toString(field.log(request, response, null)));
                    } else if (chunks.length == 2) {
                        requestLogWriter.print(ObjectUtils.toString(field.log(request, response, chunks[1])));
                    }

                    if (it.hasNext()) {
                        requestLogWriter.print("\t");
                    }
                }
                requestLogWriter.println();
            }
        }
    }
}
