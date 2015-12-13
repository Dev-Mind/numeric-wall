(function () {

  'use strict';

  angular.module('nw-admin').controller('ParameterCtrl', function ($window, $state, qrCodeParameters) {
    'ngInject';

    var ctrl = this;

    function refresh(){
      var json = $window.localStorage.getItem('parameters');
      if(!json){
        ctrl.parameterMap = qrCodeParameters;
      }
      else{
        ctrl.parameterMap = angular.fromJson(json);
      }
    }

    ctrl.save = function(){
      console.log(ctrl.parameterMap)
      $window.localStorage.setItem('parameters', angular.toJson(ctrl.parameterMap));
    };

    ctrl.cancel = function(){
      refresh();
      $state.go('admin');
    };

    refresh();
  });

})();