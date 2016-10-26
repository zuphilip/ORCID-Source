<#--

    =============================================================================

    ORCID (R) Open Source
    http://orcid.org

    Copyright (c) 2012-2014 ORCID, Inc.
    Licensed under an MIT-Style License (MIT)
    http://orcid.org/open-source-license

    This copyright and license information (including a link to the full license)
    shall be included in its entirety in all copies or substantial portion of
    the software.

    =============================================================================

-->
<script type="text/javascript" src="//code.jquery.com/jquery-2.2.3.min.js"></script>
<script type="text/javascript">
if (typeof jQuery == 'undefined') {
    document.write(unescape("%3Cscript src='${staticCdn}/javascript/jquery/2.2.3/jquery.min.js' type='text/javascript'%3E%3C/script%3E"));
}
</script>

<script type="text/javascript" src="//ajax.googleapis.com/ajax/libs/jqueryui/1.10.0/jquery-ui.min.js"></script>
<script type="text/javascript">
if (typeof jQuery.ui == 'undefined') {
    document.write(unescape("%3Cscript src='${staticCdn}/javascript/jqueryui/1.10.0/jquery-ui.min.js' type='text/javascript'%3E%3C/script%3E"));
}
</script>

<script type="text/javascript" src="//cdnjs.cloudflare.com/ajax/libs/jquery-migrate/1.3.0/jquery-migrate.min.js"></script>
<script type="text/javascript">
if (typeof jQuery == 'undefined') {
    document.write(unescape("%3Cscript src='${staticCdn}/javascript/jquery-migrate/1.3.0/jquery-migrate.min.js' type='text/javascript'%3E%3C/script%3E"));
}
</script>

<script type="text/javascript" >
    // CSRF
    var token = $("meta[name='_csrf']").attr("content");
    var header = $("meta[name='_csrf_header']").attr("content");    
    if (header && token){
	    $(document).ajaxSend(function(e, xhr, options) {
	        if (options.type != "GET") {
	           if (   options.url.startsWith(orcidVar.baseUri)
	               || options.url.startsWith(orcidVar.baseUriHttp)
	               || options.url.startsWith('/')) {
	               xhr.setRequestHeader(header, token);
	           };
	        };
	    });
    }
</script>

<script type="text/javascript" src="${staticCdn}/javascript/typeahead/0.9.3/typeahead.min.js"></script>

<script type="text/javascript" src="${staticCdn}/javascript/plugins.js?v=${ver}"></script>

<script type="text/javascript" src="${staticCdn}/javascript/orcid.js?v=${ver}"></script>

<script src="//ajax.googleapis.com/ajax/libs/angularjs/1.5.2/angular.min.js"></script>
<script src="//ajax.googleapis.com/ajax/libs/angularjs/1.5.2/angular-cookies.min.js"></script>
<script src="//ajax.googleapis.com/ajax/libs/angularjs/1.5.2/angular-sanitize.min.js"></script>

<script type="text/javascript">
if (typeof angular == 'undefined') {
    document.write(unescape("%3Cscript src='${staticCdn}/javascript/angularjs/1.5.2/angular.min.js' type='text/javascript'%3E%3C/script%3E"));
    document.write(unescape("%3Cscript src='${staticCdn}/javascript/angularjs/1.5.2/angular-cookies.min.js' type='text/javascript'%3E%3C/script%3E"));
    document.write(unescape("%3Cscript src='${staticCdn}/javascript/angularjs/1.5.2/angular-sanitize.min.js' type='text/javascript'%3E%3C/script%3E"));    
}
</script>
<script type="text/javascript" src="${staticCdn}/javascript/script.js?v=${ver}"></script>

<script type="text/javascript">
	var lang = OrcidCookie.getCookie('locale_v3');
	var script = document.createElement("script");
	script.type = "text/javascript";
    script.src = "https://www.google.com/recaptcha/api.js?onload=vcRecaptchaApiLoaded&render=explicit&hl=" + lang;
    document.body.appendChild(script);
</script>



<script src="${staticCdn}/javascript/angularjs/1.5.0/angular-recaptcha.min.js"></script>

<script type="text/javascript" src="${staticCdn}/javascript/angularOrcid.js?v=${ver}"></script>



<script type="text/javascript">
    var MTIProjectId='078e0d2f-8275-4c25-8aa9-5d902d8e4491';
    (function() {
        var mtiTracking = document.createElement('script');
        mtiTracking.type='text/javascript';
        mtiTracking.async='true';
        mtiTracking.src=('https:'==document.location.protocol?'https:':'http:')+'//fast.fonts.net/t/trackingCode.js';
        (document.getElementsByTagName('head')[0]||document.getElementsByTagName('body')[0]).appendChild( mtiTracking );
   })();
</script>

<script type="text/javascript">
   var script = document.createElement("script");
   script.type = "text/javascript";
   script.src = "https://badges.mozillascience.org/widgets/paper-badger-widget.js";
   document.body.appendChild(script);
</script>

<!--js to pdf library for save record as pdf-->
<!--<script src="https://cdnjs.cloudflare.com/ajax/libs/jspdf/1.3.2/jspdf.debug.js"></script>-->
<script src=https://cdnjs.cloudflare.com/ajax/libs/jspdf/1.2.61/jspdf.debug.js></script>

<script>

function demoFromHTML() {
	var pdf = new jsPDF('p', 'pt', 'letter');
    // source can be HTML-formatted string, or a reference
    // to an actual DOM element from which the text will be scraped.
    source = $('.public-profile')[0];


    // we support special element handlers. Register them with jQuery-style 
    // ID selector for either ID or node name. ("#iAmID", "div", "span" etc.)
    // There is no support for any other type of selectors 
    // (class, of compound) at this time.
    specialElementHandlers = {
        // element with id of "bypass" - jQuery style selector
        '#bypassme': function (element, renderer) {
            // true = "handled elsewhere, bypass text extraction"
            return true
        }
    };
    margins = {
        top: 80,
        bottom: 60,
        left: 40,
        width: 522
    };
    // all coords and widths are in jsPDF instance's declared units
    // 'inches' in this case
    pdf.fromHTML(
    source, // HTML string or DOM elem ref.
    margins.left, // x coord
    margins.top, { // y coord
        'width': margins.width, // max width of content on PDF
        'elementHandlers': specialElementHandlers
    },

    function (dispose) {
        // dispose: object with X, Y of the last line add to the PDF 
        //          this allow the insertion of new lines after html
        pdf.save('Test.pdf');
    }, margins);
}
</script>

<!-- Shibboleth -->
<#if request.requestURI?ends_with("signin") && (RequestParameters['newlogin'] )??>
	
	 
	<noscript>
	  <!-- If you need to care about non javascript browsers you will need to 
	       generate a hyperlink to a non-js DS.
	       
	       To build you will need:
	           - URL:  The base URL of the DS you use
	           - EI: Your entityId, URLencoded.  You can get this from the line that 
	             this page is called with.
	           - RET: Your return address dlib-adidp.ucs.ed.ac.uk. Again you can get
	             this from the page this is called with, but beware of the 
	             target%3Dcookie%253A5269905f bit..
	
	      < href={URL}?entityID={EI}&return={RET}
	   -->
	
	  Your Browser does not support javascript. Please use 
	  <a href="http://federation.org/DS/DS?entityID=https%3A%2F%2FyourentityId.edu.edu%2Fshibboleth&return=https%3A%2F%2Fyourreturn.edu%2FShibboleth.sso%2FDS%3FSAMLDS%3D1%26target%3Dhttps%3A%2F%2Fyourreturn.edu%2F">this link</a>.
	</noscript>
</#if>