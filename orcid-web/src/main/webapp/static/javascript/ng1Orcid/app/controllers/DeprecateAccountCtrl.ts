declare var $: any;
declare var colorbox: any;
declare var getBaseUri: any;

import * as angular from 'angular';
import {NgModule} from '@angular/core';

// This is the Angular 1 part of the module

export const DeprecateAccountCtrl = angular.module('orcidApp').controller(
    'DeprecateAccountCtrl', 
    [
        '$compile', 
        '$rootScope', 
        '$scope',
        '$timeout', 
        'emailSrvc', 
        function (
            $compile, 
            $rootScope, 
            $scope, 
            $timeout,
            emailSrvc
        ) {
            $scope.emailSrvc = emailSrvc;

            $scope.closeModal = function() {
                $.colorbox.close();
            };

            $scope.deprecateORCID = function() {
                $.ajax({
                    url: getBaseUri() + '/account/validate-deprecate-profile.json',
                    dataType: 'json',
                    data: angular.toJson($scope.deprecateProfilePojo),
                    type: 'POST',
                    contentType: 'application/json;charset=UTF-8',
                    success: function(data) {
                        $timeout(function(){
                            $scope.deprecateProfilePojo = data;
                            if (data.errors.length == 0) {
                                $.colorbox({
                                    html : $compile($('#confirm-deprecate-account-modal').html())($scope),
                                    escKey:false,
                                    overlayClose:true,
                                    close: '',
                                    });
                            }
                        });
                        $.colorbox.resize();
                    }
                }).fail(function() {
                    // something bad is happening!
                    console.log("error with change DeactivateAccount");
                });
            };

            $scope.getDeprecateProfile = function() {
                $.ajax({
                    url: getBaseUri() + '/account/deprecate-profile.json',
                    dataType: 'json',
                    success: function(data) {
                        $timeout(function(){
                            $scope.deprecateProfilePojo = data;
                        });
                    }
                }).fail(function() {
                    console.log("An error occurred preparing deprecate profile");
                });
            };

            $scope.submitModal = function() {
                $.ajax({
                    url: getBaseUri() + '/account/confirm-deprecate-profile.json',
                    type: 'POST',
                    data: angular.toJson($scope.deprecateProfilePojo),
                    contentType: 'application/json;charset=UTF-8',
                    dataType: 'json',
                    success: function(data) {                
                        emailSrvc.getEmails(function(emailData) {
                            $rootScope.$broadcast('rebuildEmails', emailData);
                        });
                        $timeout(function(){
                            $.colorbox({
                                html : $compile($('#deprecate-account-confirmation-modal').html())($scope),
                                escKey:false,
                                overlayClose:true,
                                close: '',
                                onClosed: function(){ $timeout(function(){$scope.deprecateProfilePojo = null;});},
                            });
                        });
                        $.colorbox.resize();
                    }
                }).fail(function() {
                    // something bad is happening!
                    console.log("error confirming account deprecation");
                });
            };
            
            $scope.getDeprecateProfile();
        }
    ]
);

// This is the Angular 2 part of the module
@NgModule({})
export class DeprecateAccountCtrlNg2Module {}