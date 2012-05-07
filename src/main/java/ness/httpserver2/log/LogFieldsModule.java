package ness.httpserver2.log;

import ness.httpserver2.log.LogFields.AttributeField;
import ness.httpserver2.log.LogFields.CookieField;
import ness.httpserver2.log.LogFields.ElapsedTimeField;
import ness.httpserver2.log.LogFields.MDCField;
import ness.httpserver2.log.LogFields.MethodField;
import ness.httpserver2.log.LogFields.QueryStringField;
import ness.httpserver2.log.LogFields.RemoteAddrField;
import ness.httpserver2.log.LogFields.RequestContentLengthField;
import ness.httpserver2.log.LogFields.RequestContentTypeField;
import ness.httpserver2.log.LogFields.RequestDateHeaderField;
import ness.httpserver2.log.LogFields.RequestHeaderField;
import ness.httpserver2.log.LogFields.RequestUriField;
import ness.httpserver2.log.LogFields.ResponseCodeField;
import ness.httpserver2.log.LogFields.ResponseContentTypeField;
import ness.httpserver2.log.LogFields.ResponseHeaderField;
import ness.httpserver2.log.LogFields.ThreadNameField;
import ness.httpserver2.log.LogFields.TimestampField;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;

public class LogFieldsModule extends AbstractModule
{
    @Override
    public void configure()
    {
        LogFieldBinder.bindField(binder(), "attribute").to(AttributeField.class).in(Scopes.SINGLETON);
        LogFieldBinder.bindField(binder(), "requestHeader").to(RequestHeaderField.class).in(Scopes.SINGLETON);
        LogFieldBinder.bindField(binder(), "requestDateHeader").to(RequestDateHeaderField.class).in(Scopes.SINGLETON); // Normalizes HTTP dates to ISO dates
        LogFieldBinder.bindField(binder(), "responseHeader").to(ResponseHeaderField.class).in(Scopes.SINGLETON);
        LogFieldBinder.bindField(binder(), "requestContentLength").to(RequestContentLengthField.class).in(Scopes.SINGLETON);
        LogFieldBinder.bindField(binder(), "requestContentType").to(RequestContentTypeField.class).in(Scopes.SINGLETON);
        LogFieldBinder.bindField(binder(), "responseContentType").to(ResponseContentTypeField.class).in(Scopes.SINGLETON);
        LogFieldBinder.bindField(binder(), "method").to(MethodField.class).in(Scopes.SINGLETON);
        LogFieldBinder.bindField(binder(), "requestUri").to(RequestUriField.class).in(Scopes.SINGLETON);
        LogFieldBinder.bindField(binder(), "cookie").to(CookieField.class).in(Scopes.SINGLETON);
        LogFieldBinder.bindField(binder(), "query").to(QueryStringField.class).in(Scopes.SINGLETON);
        LogFieldBinder.bindField(binder(), "remoteAddr").to(RemoteAddrField.class).in(Scopes.SINGLETON);
        LogFieldBinder.bindField(binder(), "timestamp").to(TimestampField.class).in(Scopes.SINGLETON);
        LogFieldBinder.bindField(binder(), "responseCode").to(ResponseCodeField.class).in(Scopes.SINGLETON);
        LogFieldBinder.bindField(binder(), "threadName").to(ThreadNameField.class).in(Scopes.SINGLETON);
        LogFieldBinder.bindField(binder(), "elapsedTime").to(ElapsedTimeField.class).in(Scopes.SINGLETON);
        LogFieldBinder.bindField(binder(), "mdcField").to(MDCField.class).in(Scopes.SINGLETON);
    }
}
