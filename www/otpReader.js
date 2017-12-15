var getNativeData = function(type, successCallback, failureCallback, data) {
   if (data === undefined) {
       data = '';
   }

   cordova.exec(successCallback, failureCallback, "otpreader", type, [data]);
};
