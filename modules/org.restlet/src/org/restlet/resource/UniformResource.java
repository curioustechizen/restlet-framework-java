/**
 * Copyright 2005-2009 Noelios Technologies.
 * 
 * The contents of this file are subject to the terms of the following open
 * source licenses: LGPL 3.0 or LGPL 2.1 or CDDL 1.0 (the "Licenses"). You can
 * select the license that you prefer but you may not use this file except in
 * compliance with one of these Licenses.
 * 
 * You can obtain a copy of the LGPL 3.0 license at
 * http://www.gnu.org/licenses/lgpl-3.0.html
 * 
 * You can obtain a copy of the LGPL 2.1 license at
 * http://www.gnu.org/licenses/lgpl-2.1.html
 * 
 * You can obtain a copy of the CDDL 1.0 license at
 * http://www.sun.com/cddl/cddl.html
 * 
 * See the Licenses for the specific language governing permissions and
 * limitations under the Licenses.
 * 
 * Alternatively, you can obtain a royalty free commercial license with less
 * limitations, transferable or non-transferable, directly at
 * http://www.noelios.com/products/restlet-engine
 * 
 * Restlet is a registered trademark of Noelios Technologies.
 */

package org.restlet.resource;

import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import org.restlet.Application;
import org.restlet.Context;
import org.restlet.Restlet;
import org.restlet.data.ChallengeRequest;
import org.restlet.data.ChallengeResponse;
import org.restlet.data.ClientInfo;
import org.restlet.data.Conditions;
import org.restlet.data.Cookie;
import org.restlet.data.CookieSetting;
import org.restlet.data.Dimension;
import org.restlet.data.Form;
import org.restlet.data.Method;
import org.restlet.data.Protocol;
import org.restlet.data.Range;
import org.restlet.data.Reference;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.ServerInfo;
import org.restlet.data.Status;
import org.restlet.util.Series;

/**
 * Base resource class exposing the uniform REST interface. "The central feature
 * that distinguishes the REST architectural style from other network-based
 * styles is its emphasis on a uniform interface between components. By applying
 * the software engineering principle of generality to the component interface,
 * the overall system architecture is simplified and the visibility of
 * interactions is improved. Implementations are decoupled from the services
 * they provide, which encourages independent evolvability." Roy T. Fielding<br>
 * <br>
 * Concurrency note: contrary to the {@link org.restlet.Uniform} class and its
 * main {@link Restlet} subclass where a single instance can handle several
 * calls concurrently, one instance of {@link UniformResource} is created for
 * each call handled and accessed by only one thread at a time.
 * 
 * @see <a
 *      href="http://roy.gbiv.com/pubs/dissertation/rest_arch_style.htm#sec_5_1_5">Source
 *      dissertation</a>
 * @author Jerome Louvel
 */
public abstract class UniformResource {

    /** The current context. */
    private volatile Context context;

    /** The handled request. */
    private volatile Request request;

    /** The handled response. */
    private volatile Response response;

    /**
     * Default constructor.
     */
    public UniformResource() {
        this(null, null, null);
    }

    /**
     * Constructor.
     * 
     * @param context
     *            The current context.
     * @param request
     *            The handled request.
     * @param response
     *            The handled response.
     */
    public UniformResource(Context context, Request request, Response response) {
        init(context, request, response);
    }

    /**
     * Deletes the resource and all its representations.
     * 
     * @throws ResourceException
     * @see <a
     *      href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec9.html#sec9.7">HTTP
     *      DELETE method</a>
     */
    public abstract Representation delete() throws ResourceException;

    /**
     * Represents the resource using content negotiation to select the best
     * variant based on the client preferences.
     * 
     * @return The best representation.
     * @throws ResourceException
     * @see <a
     *      href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec9.html#sec9.3">HTTP
     *      GET method</a>
     */
    public abstract Representation get() throws ResourceException;

    /**
     * Represents the resource using a given variant.
     * 
     * @param variant
     *            The variant representation to retrieve.
     * @return The representation matching the given variant.
     * @throws ResourceException
     * @see <a
     *      href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec9.html#sec9.3">HTTP
     *      GET method</a>
     */
    public abstract Representation get(Variant variant)
            throws ResourceException;

    /**
     * Returns the set of methods allowed for the current client by the
     * resource. The result can vary based on the client's user agent,
     * authentification and authorization data provided by the client.
     * 
     * @return The set of allowed methods.
     */
    public abstract Set<Method> getAllowedMethods();

    /**
     * Returns the parent application if it exists, or null.
     * 
     * @return The parent application if it exists, or null.
     */
    public Application getApplication() {
        return Application.getCurrent();
    }

    /**
     * Returns the list of authentication requests sent by an origin server to a
     * client. If none is available, an empty list is returned.
     * 
     * @return The list of authentication requests.
     * @see Response#getChallengeRequests()
     */
    public List<ChallengeRequest> getChallengeRequests() {
        return getResponse().getChallengeRequests();
    }

    /**
     * Returns the authentication response sent by a client to an origin server.
     * 
     * @return The authentication response sent by a client to an origin server.
     * @see Request#getChallengeResponse()
     */
    public ChallengeResponse getChallengeResponse() {
        return getRequest().getChallengeResponse();
    }

    /**
     * Returns the client-specific information. Creates a new instance if no one
     * has been set.
     * 
     * @return The client-specific information.
     * @see Request#getClientInfo()
     */
    public ClientInfo getClientInfo() {
        return getRequest().getClientInfo();
    }

    /**
     * Returns the modifiable conditions applying to this request. Creates a new
     * instance if no one has been set.
     * 
     * @return The conditions applying to this call.
     * @see Request#getConditions()
     */
    public Conditions getConditions() {
        return getRequest().getConditions();
    }

    /**
     * Returns the current context.
     * 
     * @return The current context.
     */
    public Context getContext() {
        return context;
    }

    /**
     * Returns the modifiable series of cookies provided by the client. Creates
     * a new instance if no one has been set.
     * 
     * @return The cookies provided by the client.
     * @see Request#getCookies()
     */
    public Series<Cookie> getCookies() {
        return getRequest().getCookies();
    }

    /**
     * Returns the modifiable series of cookie settings provided by the server.
     * Creates a new instance if no one has been set.
     * 
     * @return The cookie settings provided by the server.
     * @see Response#getCookieSettings()
     */
    public Series<CookieSetting> getCookieSettings() {
        return getResponse().getCookieSettings();
    }

    /**
     * Returns the modifiable set of selecting dimensions on which the response
     * entity may vary. If some server-side content negotiation is done, this
     * set should be properly updated, other it can be left empty. Creates a new
     * instance if no one has been set.
     * 
     * @return The set of dimensions on which the response entity may vary.
     * @see Response#getDimensions()
     */
    public Set<Dimension> getDimensions() {
        return getResponse().getDimensions();
    }

    /**
     * Returns the host reference. This may be different from the resourceRef's
     * host, for example for URNs and other URIs that don't contain host
     * information.
     * 
     * @return The host reference.
     * @see Request#getHostRef()
     */
    public Reference getHostRef() {
        return getRequest().getHostRef();
    }

    /**
     * Returns the reference that the client should follow for redirections or
     * resource creations.
     * 
     * @return The redirection reference.
     * @see Response#getLocationRef()
     */
    public Reference getLocationRef() {
        return getResponse().getLocationRef();
    }

    /**
     * Returns the logger.
     * 
     * @return The logger.
     */
    public Logger getLogger() {
        return (getContext() != null) ? getContext().getLogger() : Context
                .getCurrentLogger();
    }

    /**
     * Returns the resource reference's optional matrix.
     * 
     * @return The resource reference's optional matrix.
     * @see Reference#getMatrixAsForm()
     */
    public Form getMatrix() {
        return getReference().getMatrixAsForm();
    }

    /**
     * Returns the original reference as requested by the client. Note that this
     * property is not used during request routing.
     * 
     * @return The original reference.
     * @see Request#getOriginalRef()
     */
    public Reference getOriginalRef() {
        return getRequest().getOriginalRef();
    }

    /**
     * Returns the protocol by first returning the resourceRef.schemeProtocol
     * property if it is set, or the baseRef.schemeProtocol property otherwise.
     * 
     * @return The protocol or null if not available.
     * @see Request#getProtocol()
     */
    public Protocol getProtocol() {
        return getRequest().getProtocol();
    }

    /**
     * Returns the resource reference's optional query.
     * 
     * @return The resource reference's optional query.
     * @see Reference#getQueryAsForm()
     */
    public Form getQuery() {
        return getReference().getQueryAsForm();
    }

    /**
     * Returns the ranges to return from the target resource's representation.
     * 
     * @return The ranges to return.
     * @see Request#getRanges()
     */
    public List<Range> getRanges() {
        return getRequest().getRanges();
    }

    /**
     * Returns the URI reference.
     * 
     * @return The URI reference.
     */
    public Reference getReference() {
        return getRequest() == null ? null : getRequest().getResourceRef();
    }

    /**
     * Returns the referrer reference if available.
     * 
     * @return The referrer reference.
     */
    public Reference getReferrerRef() {
        return getRequest().getReferrerRef();
    }

    /**
     * Returns the handled request.
     * 
     * @return The handled request.
     */
    public Request getRequest() {
        return request;
    }

    /**
     * Returns the handled response.
     * 
     * @return The handled response.
     */
    public Response getResponse() {
        return response;
    }

    /**
     * Returns the application root reference.
     * 
     * @return The application root reference.
     * @see Request#getRootRef()
     */
    public Reference getRootRef() {
        return getRequest().getRootRef();
    }

    /**
     * Returns the server-specific information. Creates a new instance if no one
     * has been set.
     * 
     * @return The server-specific information.
     * @see Response#getServerInfo()
     */
    public ServerInfo getServerInfo() {
        return getResponse().getServerInfo();
    }

    /**
     * Returns the status.
     * 
     * @return The status.
     * @see Response#getStatus()
     */
    public Status getStatus() {
        return getResponse().getStatus();
    }

    /**
     * Handles the call composed of the current context, request and response.
     * 
     * @return The optional response entity.
     * @throws ResourceException
     */
    public abstract Representation handle() throws ResourceException;

    /**
     * Represents the resource using content negotiation to select the best
     * variant based on the client preferences. This method is identical to
     * {@link #get()} but doesn't return the actual content of the
     * representation, only its metadata.
     * 
     * @return The best representation.
     * @throws ResourceException
     * @see <a
     *      href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec9.html#sec9.4">HTTP
     *      HEAD method</a>
     */
    public abstract Representation head() throws ResourceException;

    /**
     * Represents the resource using a given variant. This method is identical
     * to {@link #get(Variant)} but doesn't return the actual content of the
     * representation, only its metadata.
     * 
     * @param variant
     *            The variant representation to retrieve.
     * @return The representation matching the given variant.
     * @throws ResourceException
     * @see <a
     *      href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec9.html#sec9.4">HTTP
     *      HEAD method</a>
     */
    public abstract Representation head(Variant variant)
            throws ResourceException;

    /**
     * Initialization method that is invoked either by the
     * {@link #UniformResource(Context, Request, Response)} constructor or right
     * after the default {@link #UniformResource()} constructor. It sets the
     * environment of the current resource instance.
     * 
     * @param context
     *            The current context.
     * @param request
     *            The handled request.
     * @param response
     *            The handled response.
     */
    public void init(Context context, Request request, Response response) {
        this.context = context;
        this.request = request;
        this.response = response;
    }

    /**
     * Indicates if the message was or will be exchanged confidentially, for
     * example via a SSL-secured connection.
     * 
     * @return True if the message is confidential.
     * @see Request#isConfidential()
     */
    public boolean isConfidential() {
        return getRequest().isConfidential();
    }

    /**
     * Describes the resource using content negotiation to select the best
     * variant based on the client preferences.
     * 
     * @return The best description.
     * @throws ResourceException
     * @see <a
     *      href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec9.html#sec9.2">HTTP
     *      OPTIONS method</a>
     */
    public abstract Representation options() throws ResourceException;

    /**
     * Describes the resource using a given variant.
     * 
     * @param variant
     *            The description variant to match.
     * @return The matched description or null.
     * @throws ResourceException
     * @see <a
     *      href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec9.html#sec9.2">HTTP
     *      OPTIONS method</a>
     */
    public abstract Representation options(Variant variant)
            throws ResourceException;

    /**
     * Posts a representation to the resource at the target URI reference.
     * 
     * @param entity
     *            The posted entity.
     * @return The optional result entity.
     * @throws ResourceException
     * @see <a
     *      href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec9.html#sec9.5">HTTP
     *      POST method</a>
     */
    public abstract Representation post(Representation entity)
            throws ResourceException;

    /**
     * Creates or updates a resource with the given representation as new state
     * to be stored.
     * 
     * @param representation
     *            The representation to store.
     * @return The optional result entity.
     * @throws ResourceException
     * @see <a
     *      href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec9.html#sec9.6">HTTP
     *      PUT method</a>
     */
    public abstract Representation put(Representation representation)
            throws ResourceException;

    /**
     * Releases the request and response entities. If the entity is transient
     * and hasn't been read yet, all the remaining content will be discarded,
     * any open socket, channel, file or similar source of content will be
     * immediately closed.
     * 
     * @see Request#release()
     * @see Response#release()
     */
    public void release() {
        getRequest().release();
        getResponse().release();
    }

}
