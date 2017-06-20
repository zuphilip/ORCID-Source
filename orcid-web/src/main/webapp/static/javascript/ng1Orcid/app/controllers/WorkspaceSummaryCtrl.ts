import * as angular from 'angular';
import {NgModule} from '@angular/core';

// This is the Angular 1 part of the module

export const WorkspaceSummaryCtrl = angular.module('orcidApp').controller(
    'WorkspaceSummaryCtrl', 
    [
        '$compile', 
        '$scope', 
        'affiliationsSrvc', 
        'fundingSrvc', 
        'peerReviewSrvc', 
        'workspaceSrvc',
        'worksSrvc', 
        function (
            $compile, 
            $scope, 
            affiliationsSrvc, 
            fundingSrvc, 
            peerReviewSrvc, 
            workspaceSrvc,
            worksSrvc
        ){
            $scope.workspaceSrvc = workspaceSrvc;
            $scope.worksSrvc = worksSrvc;
            $scope.affiliationsSrvc = affiliationsSrvc;
            $scope.fundingSrvc = fundingSrvc;
            $scope.peerReviewSrvc = peerReviewSrvc;
            
            $scope.showAddAlert = function () {
                if ($scope.worksSrvc.loading == false 
                    && $scope.affiliationsSrvc.loading == false 
                    && $scope.peerReviewSrvc.loading == false
                    && $scope.worksSrvc.groups.length == 0
                    && $scope.affiliationsSrvc.educations.length == 0
                    && $scope.affiliationsSrvc.employments.length == 0
                    && $scope.fundingSrvc.groups.length == 0
                    && $scope.peerReviewSrvc.groups.lenght == 0)
                {

                    return true;
                    
                }
                return false;
            };
        }
    ]
);

// This is the Angular 2 part of the module
@NgModule({})
export class WorkspaceSummaryCtrlNg2Module {}