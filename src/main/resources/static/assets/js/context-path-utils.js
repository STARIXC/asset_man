/**
 * Context Path Utilities
 * Handles dynamic context paths for Tomcat deployment
 */
(function(window) {
    'use strict';

    function getContextPath() {
        return window.APP_CONTEXT_PATH || '/';
    }

    function buildUrl(path) {
        if (!path) return getContextPath();
        const cleanPath = path.startsWith('/') ? path.substring(1) : path;
        const contextPath = getContextPath();
        const baseUrl = contextPath.endsWith('/') ? contextPath : contextPath + '/';
        return baseUrl + cleanPath;
    }

    function getCsrfToken() {
        return window.APP_CSRF_TOKEN || '';
    }

    function getCsrfHeader() {
        return window.APP_CSRF_HEADER || 'X-CSRF-TOKEN';
    }

    function ajaxGet(url, options = {}) {
        const headers = {
            'Content-Type': 'application/json',
            ...options.headers
        };

        const csrfToken = getCsrfToken();
        const csrfHeader = getCsrfHeader();
        if (csrfToken) {
            headers[csrfHeader] = csrfToken;
        }

        return fetch(buildUrl(url), {
            method: 'GET',
            headers: headers,
            ...options
        }).then(response => {
            if (!response.ok) {
                throw new Error(`HTTP ${response.status}: ${response.statusText}`);
            }
            return response.json();
        });
    }

    function ajaxPost(url, data, options = {}) {
        const headers = {
            'Content-Type': 'application/json',
            ...options.headers
        };

        const csrfToken = getCsrfToken();
        const csrfHeader = getCsrfHeader();
        if (csrfToken) {
            headers[csrfHeader] = csrfToken;
        }

        return fetch(buildUrl(url), {
            method: 'POST',
            headers: headers,
            body: JSON.stringify(data),
            ...options
        }).then(response => {
            if (!response.ok) {
                throw new Error(`HTTP ${response.status}: ${response.statusText}`);
            }
            return response.json();
        });
    }

    function ajaxDelete(url, options = {}) {
        const headers = {
            'Content-Type': 'application/json',
            ...options.headers
        };

        const csrfToken = getCsrfToken();
        const csrfHeader = getCsrfHeader();
        if (csrfToken) {
            headers[csrfHeader] = csrfToken;
        }

        return fetch(buildUrl(url), {
            method: 'DELETE',
            headers: headers,
            ...options
        }).then(response => {
            if (!response.ok) {
                throw new Error(`HTTP ${response.status}: ${response.statusText}`);
            }
            return response.json();
        });
    }

    // Expose utilities
    window.AppUtils = {
        getContextPath,
        buildUrl,
        getCsrfToken,
        getCsrfHeader,
        ajaxGet,
        ajaxPost,
        ajaxDelete
    };

    // Backward compatibility
    window.buildUrl = buildUrl;
    window.ajaxGet = ajaxGet;
    window.ajaxPost = ajaxPost;
    window.ajaxDelete = ajaxDelete;

})(window);