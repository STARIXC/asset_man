/**
 * MutationObserver Wrapper
 * Prevents errors when observe() is called with invalid nodes
 */
(function() {
    'use strict';
    
    // Store original MutationObserver
    const OriginalMutationObserver = window.MutationObserver || window.WebKitMutationObserver;
    
    if (!OriginalMutationObserver) {
        console.warn('MutationObserver not supported in this browser');
        return;
    }
    
    // Create wrapper constructor
    function SafeMutationObserver(callback) {
        const observer = new OriginalMutationObserver(callback);
        const originalObserve = observer.observe;
        const originalDisconnect = observer.disconnect;
        const originalTakeRecords = observer.takeRecords;
        
        // Wrap observe method
        observer.observe = function(target, options) {
            // Validate target
            if (!target) {
                console.warn('MutationObserver.observe: target is null or undefined');
                return;
            }
            
            if (!(target instanceof Node)) {
                console.warn('MutationObserver.observe: target is not a Node', target);
                return;
            }
            
            // Validate options
            if (!options || typeof options !== 'object') {
                console.warn('MutationObserver.observe: options must be an object');
                return;
            }
            
            try {
                return originalObserve.call(this, target, options);
            } catch (error) {
                console.error('MutationObserver.observe error:', error);
                console.error('Target:', target);
                console.error('Options:', options);
                // Don't throw - just log and continue
            }
        };
        
        // Preserve other methods
        observer.disconnect = function() {
            try {
                return originalDisconnect.call(this);
            } catch (error) {
                console.error('MutationObserver.disconnect error:', error);
            }
        };
        
        observer.takeRecords = function() {
            try {
                return originalTakeRecords.call(this);
            } catch (error) {
                console.error('MutationObserver.takeRecords error:', error);
                return [];
            }
        };
        
        return observer;
    }
    
    // Copy static properties
    SafeMutationObserver.prototype = OriginalMutationObserver.prototype;
    
    // Replace global MutationObserver
    window.MutationObserver = SafeMutationObserver;
    
    console.log('MutationObserver wrapper initialized');
})();
