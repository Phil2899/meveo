const buildRequestParameters = (parameters, schema) => {
    if (schema) {
        const errors = []
        const requestParameters = Object.keys(
            schema.properties,
        ).reduce((reqParameters, property) => {
            const value = parameters[property]
            const isRequired = schema.properties[property].required

            if (isRequired && !value) {
                errors.push(`${property} is required.`)
            } else if (!!value) {
                return {
                    ...reqParameters,
                    [property]: value
                }
            }
            return reqParameters
        }, {})
        if (errors.length > 0) {
            throw errors
        }
        return requestParameters
    }
    return null
}

const EVENT = {
    SUCCESS: "Update#{ENDPOINT_CODE}-SUCCESS",
    ERROR: "Update#{ENDPOINT_CODE}-ERROR"
};

export const registerEventListeners = (
    component,
    successCallback,
    errorCallback
) => {
    if (successCallback) {
        component.addEventListener(EVENT.SUCCESS, successCallback);
    }
    if (errorCallback) {
        component.addEventListener(EVENT.ERROR, errorCallback);
    }
};

export const getRequestSchema = async (parameters, config) => {
    return #{REQUEST_SCHEMA}
};

export const getResponseSchema = async (parameters, config) => {
    return #{RESPONSE_SCHEMA}
}

export const executeApiCall = async (
    component,
    params,
    successCallback, // optional
    errorCallback // optional
) => {
    registerEventListeners(component, successCallback, errorCallback);
    const parameters = params || {};
    const {
        token,
        config
    } = parameters;

    // the name of the config variable is the name of the module
    const {
        Update#{ENDPOINT_CODE}: {
            OVERRIDE_URL,
            USE_MOCK
        }
    } = config || {};

    // the baseUrl can be overridden by indicating a OVERRIDE_URL in config,
    // by default it will use the same URL as the client application
    // or if this is auto-generated by meveo, it will have the server's host url
    const baseUrl = OVERRIDE_URL || window.location.origin; // || server.host.url

    // just an example how to use the useMock parameter to switch between mock and actual endpoints.
    const apiUrl = USE_MOCK ?
        `${baseUrl}/auth/realms/meveo/account?useMock=true` :
        `${baseUrl}/auth/realms/meveo/account`;

    //fetch request schema to filter out optional parameters that should not be passed into the request
    try {
        const requestSchema = await getRequestSchema(parameters);
        const requestParameters = buildRequestParameters(parameters, requestSchema);
        const parameterKeys = Object.keys(requestParameters || {});
        const hasParameters = requestParameters && parameterKeys.length > 0;

        const requestUrl = new URL(apiUrl);
        if (hasParameters) {
            parameterKeys.forEach(key => {
                requestUrl.searchParams.append(key, requestParameters[key]);
            });
        }

        const headers = new Headers();
        headers.append("Content-Type", "application/json");
        headers.append("Accept", "application/json");
        headers.append("Authorization", `Bearer ${token}`);

        const options = {
            method: "GET",
            headers
        };

        const response = await fetch(requestUrl, options);
        if (!response.ok) {
            throw [
                `Encountered error calling API: ${apiUrl}`,
                `Status code: ${response.status} [${response.statusText}]`
            ];
        }
        // if accept = "application/json" otherwise return response.text()
        const result = await response.json();
        component.dispatchEvent(
            new CustomEvent(EVENT.SUCCESS, {
                detail: {
                    result
                },
                bubbles: true
            })
        );
    } catch (error) {
        component.dispatchEvent(
            new CustomEvent(EVENT.ERROR, {
                detail: {
                    error
                },
                bubbles: true
            })
        );
    }
};