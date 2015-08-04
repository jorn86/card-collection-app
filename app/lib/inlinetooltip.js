/*!
 Simple Inline Tooltips v1.0
 http://www.infinitegyre.com/2014/03/simple-inline-tooltips.html
 Copyright (c) 2014 Nickolas Reynolds
 Released under the MIT license
 Based on Footnotes by Lukas Mathis
 http://ignorethecode.net/blog/2010/04/20/footnotes/
 */

/*
 DESCRIPTION

 This script allows you to attach custom tooltips to text (or any
 HTML element), inline and with almost no configuration whatsoever.
 HOW TO USE
 <span>
 Tooltip trigger
 <div class="iltt">Tooltip contents</div>
 </span>
 DEPENDENCIES
 jQuery 1.7 or later
 inlinetooltip.css (optional)
 FAQS
 ? Can I change the span to a div, or the div to a span ?
 * Yes, feel free to use span or div for either container.
 ? Can I edit the style or other css attributes of the containers ?
 * Yes, you can add whatever css attributes and styles you want, EXCEPT:
 - The container for the tooltip contents ONLY must have the
 "iltt" (InLineToolTip) css class. No other elements should
 have the "iltt" class.
 - The css id for BOTH containers is used internally. Anything
 you put in there will be overwritten.
 - The css data attribute "data-linkid" is used internally for
 BOTH containers, and its existence or nonexistence drives
 some internal logic. Don't use it at all!

 ? Do the tooltip trigger and tooltip contents have to be text ?
 * No, feel free to use links, images, entire other spans or divs,
 or pretty much anything that doesn't care about its exact location
 in the DOM tree.
 ? Can I put a tooltip within a tooltip ?
 * Yes, but be very careful to wrap everything correctly:
 <span>
 Tooltip trigger
 <div class="iltt">Tooltip contents with
 <span>
 another tooltip trigger
 <div class="iltt">Second tooltip contents</div>
 </span>
 </div>
 </span>
 */

$( document ).ready( function() {

    iltt.initialize( $( document ) );

    // Track mouse movement for the tooltip positioning logic:

    $( document ).mousemove( function( event ) {

        iltt.mousex = event.pageX;
        iltt.mousey = event.pageY;

    } );

} );

var iltt = {

    stickiness: 150,    // Number of ms to wait before starting to fade
    fadetime: 150,      // Duration of the fade animation in ms
    opacity: 1.0,       // Default opacity of the tooltip

    timeouts: {},
    mousex: 0,
    mousey: 0,
    id: 0,

    initialize: function( scope ) {

        // Within the given scope, select the parents of all elements with css
        // class "iltt" (and not "ilttinitialized"):

        var containers = scope.find( ".iltt" ).not( ".ilttinitialized" ).parent();

        // For each of those parent containers ...

        containers.each( function( index, container ) {

            // ... identify the corresponding tooltip container:

            var tooltip = $( container ).children( ".iltt" );

            // Unbind any existing mouseenter and mouseleave events, then bind
            // custom ones:

            $( container ).off( "mouseenter" ).mouseenter( iltt.mouseenter );
            $( container ).off( "mouseleave" ).mouseleave( iltt.mouseleave );
            tooltip.off( "mouseenter" ).mouseenter( iltt.entertooltip );
            tooltip.off( "mouseleave" ).mouseleave( iltt.leavetooltip );

            // Take the current iltt.id and assign corresponding "parent" and
            // "child" css ids:

            $( container ).attr( "id", "iltt_parent" + iltt.id );
            tooltip.attr( "id", "iltt_child" + iltt.id );

            // Assign identical data-linkid attributes:

            $( container ).data( "linkid", iltt.id );
            tooltip.data( "linkid", iltt.id );

            // When the parent (the trigger) wordwraps, css-reported position
            // attributes are not very useful, making positioning the tooltip
            // somewhat difficult. Let's just disallow it:

            $( container ).css( "white-space", "nowrap" );

            // Whether using mouseover/mouseout, or mouseenter/mouseleave (this
            // script uses the latter), the events trigger (or fail to trigger)
            // in not-so-useful ways when dealing with elements that are nested
            // together in the DOM tree.
            //
            // To get around that the tooltip is simply moved to the end of the
            // document body. (Which means it's no longer nested in the parent
            // container which holds the trigger.)

            tooltip.appendTo( document.body );

            // Make sure the tooltip is hidden and has opacity 0. Some logic
            // elsewhere in the script uses opacity as a proxy to tell whether
            // or not the tooltip is currently hidden, so don't remove this.

            tooltip.hide();
            tooltip.css( { opacity: 0 } );

            // Add a css class to the tooltip to indicate that it's been
            // initialized. This will prevent the script from trying to process
            // it again later, say, if another script dynamically generates a
            // new tooltip and then reruns iltt.initialize.

            tooltip.addClass( "ilttinitialized" );

            // Lastly, increment iltt.id so no other trigger/tooltip pair can
            // have this one:

            iltt.id += 1;

        } );

    },

    // Mouse over a trigger:

    mouseenter: function() {

        var linkid = $( this ).data( "linkid" );
        var tooltip = $( "#iltt_child" + linkid );

        // If the trigger's corresponding tooltip has nonzero opacity,
        // it's already positioned and showing ...

        if( tooltip.css( "opacity" ) != 0 ) {

            // ... though it may be fading, so unfade it:

            iltt.unfade( linkid );

            // And exit the entire function:

            return;

        }

        // Css-reported position attributes will be undefined (or worse)
        // if the tooltip isn't first positioned and shown. Keep this
        // stuff before the repositioning code:

        tooltip.css( { position: "absolute", opacity: iltt.opacity } );
        tooltip.offset( { left: 0, top: 0 } );
        tooltip.show();

        // What follows is a bunch of tests to check whether the tooltip
        // fits within the browser viewport. If not, different positions
        // are tried sequentially until one is found that fits. If none
        // of them fit, the tooltip defaults to being positioned below
        // below its parent (the trigger), left-aligned.

        var p_top = $( this ).offset().top;         // Parent top
        var p_left = $( this ).offset().left;       // Parent left
        var p_width = $( this ).outerWidth();       // Parent width
        var p_height = $( this ).outerHeight();     // Parent height

        var w_width = $( window ).width();          // Window (viewport) width
        var w_height = $( window ).height();        // Window (viewport) height
        var v_scroll = $( window ).scrollTop();     // Vertical scroll
        var h_scroll = $( window ).scrollLeft();    // Horizontal scroll

        var top = 0;                                // Tooltip top
        var left = 0;                               // Tooltip left
        var width = tooltip.outerWidth();           // Tooltip width
        var height = tooltip.outerHeight();         // Tooltip height

        // Position the tooltip under its parent, left-aligned.

        left = p_left;
        top = p_top + p_height;

        // If the tooltip extends past the right edge of the viewport
        // boundary, slide it left.

        if( left + width > w_width + h_scroll ) {

            left = w_width + h_scroll - width;

        }

        // If the tooltip extends past the left edge of the viewport
        // boundary, slide it right. (This might cause the right side
        // to extend past the viewport again, but if it's one or the
        // other, bringing the left side of the tooltip within view is
        // more important.)

        if( left < h_scroll ) {

            left = h_scroll;

        }

        // If the tooltip extends past the bottom edge of the viewport
        // boundary, flip it above its parent.

        if( top + height > w_height + v_scroll ) {

            top = p_top - height;

        }

        // If the tooltip extends past the top edge of the viewport
        // boundary, move it to the immediate right of its parent
        // and vertically center it.

        if( top < v_scroll ) {

            left = p_left + p_width;
            top = v_scroll + ( w_height - height ) / 2;

        }

        // If the tooltip extends past the right edge of the viewport
        // boundary, flip it to the left side of its parent and
        // vertically center it.

        if( left + width > w_width + h_scroll ) {

            left = p_left - width;
            top = v_scroll + ( w_height - height ) / 2;

        }

        // If the tooltip extends past the left edge of the viewport
        // boundary, move it to just right of the mouse cursor and
        // vertically center it.

        if( left < h_scroll ) {

            left = iltt.mousex + 9;
            top = v_scroll + ( w_height - height ) / 2;

        }

        // If the tooltip extends past the right edge of the viewport
        // boundary, move it to just left of the mouse cursor and
        // vertically center it.

        if( left + width > w_width + h_scroll ) {

            left = iltt.mousex - width - 9;
            top = v_scroll + ( w_height - height ) / 2;

        }

        // If the tooltip extends past the left edge of the viewport
        // boundary, give up and reposition it back under under its
        // parent, left-aligned.

        if( left < h_scroll ) {

            left = p_left;
            top = p_top + p_height;

        }

        // Lock in the final position:

//                              ˅˅˅˅-------˅˅˅--- variables manipulated above
        tooltip.offset( { left: left, top: top } );
//                        ˄˄˄˄--------˄˄˄--- css attributes

    },

    // Mouse out a trigger:

    mouseleave: function() {

        // Grab the trigger's linkid:

        var linkid = $( this ).data( "linkid" );

        // Fade the corresponding tooltip:

        iltt.fade( linkid );

    },

    // Mouse over a tooltip:

    entertooltip: function() {

        // Grab the tooltip's linkid:

        var linkid = $( this ).data( "linkid" );

        while( typeof linkid != "undefined" ) {

            // Unfade the current tooltip:

            iltt.unfade( linkid );

            // Grab the linkid of the grandparent. If the grandparent doesn't
            // have one (it's undefined), the while loop will terminate. Else,
            // keep traversing back through ancestors with linkids, unfading
            // them all. (Thus hovering your mouse over the last in a long
            // chain of nested tooltips will keep them all alive.)

            linkid = $( "#iltt_parent" + linkid ).parent().data( "linkid" );

        }

    },

    // Mouse out a tooltip:

    leavetooltip: function() {

        // Grab the tooltip's linkid:

        var linkid = $( this ).data( "linkid" );

        while( typeof linkid != "undefined" ) {

            // Fade the current tooltip:

            iltt.fade( linkid );

            // Same idea as before, find the next tooltip ancestor and loop:

            linkid = $( "#iltt_parent" + linkid ).parent().data( "linkid" );

        }

    },

    // Fade a tooltip. I advise against moving this code outside of its own
    // function unless you are familiar with JavaScript closures.

    fade: function( linkid ) {

        // Set a timeout that, after iltt.stickiness milliseconds, will animate
        // the tooltip's opacity down to 0 over iltt.fadetime milliseconds. At
        // the end of the animation, hide the tooltip.

        iltt.timeouts[ linkid ] = setTimeout( function () {

            $( "#iltt_child" + linkid ).animate( { opacity: 0 }, iltt.fadetime, function() {

                $( "#iltt_child" + linkid ).hide();

            } );

        }, iltt.stickiness );

    },

    // Unfade a tooltip:

    unfade: function( linkid ) {

        $( "#iltt_child" + linkid ).stop();                             // Stop fade animation
        clearTimeout( iltt.timeouts[ linkid ] );                        // Clear setTimeout
        $( "#iltt_child" + linkid ).css( { opacity: iltt.opacity } );   // Restore opacity

    }

};

/*!
 Inline Magic: The Gathering Tooltips v1.0

 for use with Simple Inline Tooltips v1.0
 http://www.infinitegyre.com/2014/03/simple-inline-tooltips.html
 Copyright (c) 2014 Nickolas Reynolds
 Released under the MIT license
 */

/*
 DESCRIPTION
 Specify the name of a Magic card, and this script will attach a
 tooltip with the card's image, and link to the card's Gatherer page.
 HOW TO USE
 <span class="inlinemtg">Lightning Bolt</span>
 DEPENDENCIES
 jQuery 1.7 or later
 Simple Inline Tooltips v1.0
 ADVANCED OPTIONS
 * Custom link text:
 <span class="inlinemtg" data-name="Lightning Bolt">Custom link text</span>
 * Version from a particular set:
 <span class="inlinemtg" data-set="Alpha">Lightning Bolt</span>
 Note: There is a fairly extensive data table that attempts to
 map whatever you specify in data-set to a Gatherer set
 code. Most normal ways you can think of to refer to a
 set name are probably in the data table and will work,
 e.g. "ftv20", "revised", "sixth", "scars", "masques" ...
 Note: This only changes the picture in the tooltip. The link will
 still lead to whatever the latest version of the card is.
 This is due to a limitation in Gatherer, so there's no easy
 way to fix it.
 * Link by multiverse id:
 <span class="inlinemtg" data-multiverseid="225652">Italian Lightning Bolt</span>
 * Rotate a split card:
 <span class="inlinemtg" data-options="rotate90">Fire // Ice</span>
 Note: Split cards are problematic. Gatherer's image handler will
 accept search strings like "Fire // Ice", but the search
 handler (where the link goes) chokes on it. The only way
 to *link* to a split card is to specify its multiverse id,
 for example:
 <span class"inlinemtg" data-multiverseid="292753">Fire and Ice, a great card</span>
 You may chain any and all of the advanced options together, though
 multiverse id will override name and set.
 */

$( document ).ready( function() {

    inlinemtg.linkcards( $( document ) );

} );

var inlinemtg = {

    linkcards: function ( scope ) {

        // Within the given scope, select all elements with css class
        // "inlinemtg" (and not "inlinemtgprocessed"):

        var cards = scope.find( ".inlinemtg" ).not( ".inlinemtgprocessed" );

        // For each of those elements ...

        cards.each( function( index, element ) {

            // Parse all of the user specified data:

            var options = $( element ).data( "options" );           // data-options
            var name = $( element ).data( "name" );                 // data-name
            var set = $( element ).data( "set" );                   // data-set
            var multiverseid = $( element ).data( "multiverseid" ); // data-multiverseid
            var contents = $( element ).html();                     // Span contents (link text)
            var a_params = "";

            // If no multiverseid is specified ...

            if( typeof multiverseid == "undefined" ) {

                // ... set name = contents, or contents = name, if either
                // is missing:

                if( typeof name == "undefined" ) name = contents;
                if( contents == "" ) contents = name;

                // Start building the parameter string that will be given
                // to the Gatherer image handler and search handler:

                a_params += "&name=" + name;

                // If a set is specified ...

                if( typeof set != "undefined" ) {

                    // ... strip punctuation, spaces, convert to upper-case:

                    var strip = set.replace(/[\.,-\/#!$%\^&\*;:{}=\-_`'~()\s]/g,"")
                        .toUpperCase();

                    // If the stripped string is in inlinemtg.sets, then use
                    // the corresponding set code:

                    if( strip in inlinemtg.sets ) set = inlinemtg.sets[ strip ];

                    // Otherwise just use whatever was specified, and continue
                    // building the parameter string:

                    a_params += "&set=" + set;

                }

                // If multiverseid *is* specified:

            } else {

                // Build the parameter string accordingly, and set
                // contents = multiverseid if contents is missing:

                a_params += "&multiverseid=" + multiverseid;
                if( contents == "" ) contents = multiverseid;

            }

            // Make a parameter string for the image handler, and if options
            // are specified, tack them onto the image parameters:

            var img_params = a_params;
            if( typeof options != undefined ) img_params += "&options=" + options;

            // Put everything together and overwrite the span contents
            // (link text), resulting in the following structure:
            //
            // <span class="inlinemtg">
            //     <a href="...">Previous Contents</a>
            //     <span class="iltt"><a href="..."><img src="..."></a></span>
            // </span>

            var a_start = '<a href="http://gatherer.wizards.com/Pages/Card/Details.aspx?type=card';
            var a_end = '" target="_blank">';
            var img_start = '<img src="http://gatherer.wizards.com/Handlers/Image.ashx?type=card';

            var htmlstring = [ a_start + a_params + a_end + contents + '</a><span class="iltt">',
                a_start + a_params + a_end + img_start + img_params + '"></a></span>'
            ].join( "" );

            $( element ).html( htmlstring );

            // Add a css class to the indicate that the inlinemtg span has been
            // processed. This prevents the script from trying to reprocess it
            // if inlinemtg.linkcards is called again for whatever reason.

            $( element ).addClass( "inlinemtgprocessed" );

            // Tell inlinetooltip.js to initialize the element, since it now
            // has an uninitialized tooltip inside of it:

            iltt.initialize( $( element ) );

        } );

    },

    // Data table with common ways of referring to all of the extant Magic
    // card sets as of 2014-03-01, mapped to the corresponding Gatherer set
    // code.
    //
    // Note: data-set is stripped and sanitized before the lookup, so, for
    //       example, data-set="Urza's Saga" will be converted to URZASSAGA
    //       and then correctly identified with Gatherer set code "UZ".

    sets: { "10": "10E",
        "10E": "10E",
        "10TH": "10E",
        "10THEDITION": "10E",
        "CORESETTENTHEDITION": "10E",
        "TEN": "10E",
        "TENTH": "10E",
        "TENTHEDITION": "10E",
        "1": "1E",
        "1ST": "1E",
        "1STEDITION": "1E",
        "ALPHA": "1E",
        "FIRST": "1E",
        "FIRSTEDITION": "1E",
        "LEA": "1E",
        "LIMITEDEDITIONALPHA": "1E",
        "ONE": "1E",
        "2": "2E",
        "2ND": "2E",
        "2NDEDITION": "2E",
        "BETA": "2E",
        "LEB": "2E",
        "LIMITEDEDITIONBETA": "2E",
        "SECOND": "2E",
        "SECONDEDITION": "2E",
        "TWO": "2E",
        "2ED": "2U",
        "UNLIMITED": "2U",
        "3ED": "3E",
        "REV": "3E",
        "REVISED": "3E",
        "REVISEDEDITION": "3E",
        "THIRD": "3E",
        "THIRDEDITION": "3E",
        "4": "4E",
        "4ED": "4E",
        "4TH": "4E",
        "4THEDITION": "4E",
        "FOUR": "4E",
        "FOURTH": "4E",
        "FOURTHEDITION": "4E",
        "5DN": "5DN",
        "FIFTHDAWN": "5DN",
        "5": "5E",
        "5ED": "5E",
        "5TH": "5E",
        "5THEDITION": "5E",
        "FIFTH": "5E",
        "FIFTHEDITION": "5E",
        "FIVE": "5E",
        "6": "6E",
        "6ED": "6E",
        "6TH": "6E",
        "6THEDITION": "6E",
        "CLASSICSIXTHEDITION": "6E",
        "SIX": "6E",
        "SIXTH": "6E",
        "SIXTHEDITION": "6E",
        "7": "7E",
        "7ED": "7E",
        "7TH": "7E",
        "7THEDITION": "7E",
        "SEVEN": "7E",
        "SEVENTH": "7E",
        "SEVENTHEDITION": "7E",
        "8": "8ED",
        "8ED": "8ED",
        "8TH": "8ED",
        "8THEDITION": "8ED",
        "CORESETEIGHTHEDITION": "8ED",
        "EIGHT": "8ED",
        "EIGHTH": "8ED",
        "EIGHTHEDITION": "8ED",
        "9": "9ED",
        "9ED": "9ED",
        "9TH": "9ED",
        "9THEDITION": "9ED",
        "CORESETNINTHEDITION": "9ED",
        "NINE": "9ED",
        "NINTH": "9ED",
        "NINTHEDITION": "9ED",
        "ALL": "AL",
        "ALLIANCES": "AL",
        "ALA": "ALA",
        "ALARA": "ALA",
        "SHARDS": "ALA",
        "SHARDSOFALARA": "ALA",
        "ARABIANNIGHTS": "AN",
        "ARN": "AN",
        "APC": "AP",
        "APOCALYPSE": "AP",
        "ANTIQ": "AQ",
        "ANTIQUITIES": "AQ",
        "ATQ": "AQ",
        "ALARAREBORN": "ARB",
        "ARB": "ARB",
        "REBORN": "ARB",
        "ARC": "ARC",
        "ARCHENEMY": "ARC",
        "ARE": "ARE",
        "ARENA": "ARE",
        "ANTHOLOGIES": "ATH",
        "ATH": "ATH",
        "BEATDOWN": "BD",
        "BTD": "BD",
        "BIN": "BIN",
        "BOOKINSERTS": "BIN",
        "BNG": "BNG",
        "BORNOFTHEGODS": "BNG",
        "BETRAYERS": "BOK",
        "BETRAYERSOFKAMIGAWA": "BOK",
        "BOK": "BOK",
        "BATTLEROYALE": "BR",
        "BRB": "BR",
        "C13": "C13",
        "COMMANDER2013EDITION": "C13",
        "COMMANDER2013": "C13",
        "CFX": "CFX",
        "CONFLUX": "CFX",
        "DESTINY": "CG",
        "UDS": "CG",
        "URZASDESTINY": "CG",
        "CHR": "CH",
        "CHRONICLES": "CH",
        "CHAMPIONS": "CHK",
        "CHAMPIONSOFKAMIGAWA": "CHK",
        "CHK": "CHK",
        "CHAMPSPROMOS": "CHP",
        "CHP": "CHP",
        "CIN": "CIN",
        "COMICINSERTS": "CIN",
        "CM1": "CM1",
        "COMMANDERSARSENAL": "CM1",
        "CMD": "CMD",
        "COMMANDER": "CMD",
        "CNS": "CNS",
        "CONSPIRACY": "CNS",
        "COLDSNAP": "CSP",
        "CSP": "CSP",
        "COLDSNAPTHEMEDECKS": "CST",
        "CST": "CST",
        "CONVENTIONPROMOS": "CVP",
        "CVP": "CVP",
        "DD2": "DD2",
        "DUELDECKSJACEVSCHANDRA": "DD2",
        "DUELDECKJACEVSCHANDRA": "DD3",
        "JACEVSCHANDRA": "DD4",
        "DDC": "DDC",
        "DIVINEVSDEMONIC": "DDC",
        "DUELDECKDIVINEVSDEMONIC": "DDC",
        "DUELDECKSDIVINEVSDEMONIC": "DDC",
        "DDD": "DDD",
        "DUELDECKGARRUKVSLILIANA": "DDD",
        "DUELDECKSGARRUKVSLILIANA": "DDD",
        "GARRUKVSLILIANA": "DDD",
        "DDE": "DDE",
        "DUELDECKPHYREXIAVSTHECOALITION": "DDE",
        "DUELDECKSPHYREXIAVSTHECOALITION": "DDE",
        "PHYREXIAVSTHECOALITION": "DDE",
        "DDF": "DDF",
        "DUELDECKELSPETHVSTEZZERET": "DDF",
        "DUELDECKSELSPETHVSTEZZERET": "DDF",
        "ELSPETHVSTEZZERET": "DDF",
        "DDG": "DDG",
        "DUELDECKKNIGHTSVSDRAGONS": "DDG",
        "DUELDECKSKNIGHTSVSDRAGONS": "DDG",
        "KNIGHTSVSDRAGONS": "DDG",
        "AJANIVSNICOLBOLAS": "DDH",
        "DDH": "DDH",
        "DUELDECKAJANIVSNICOLBOLAS": "DDH",
        "DUELDECKSAJANIVSNICOLBOLAS": "DDH",
        "DDI": "DDI",
        "DUELDECKVENSERVSKOTH": "DDI",
        "DUELDECKSVENSERVSKOTH": "DDI",
        "VENSERVSKOTH": "DDI",
        "DDJ": "DDJ",
        "DUELDECKIZZETVSGOLGARI": "DDJ",
        "DUELDECKSIZZETVSGOLGARI": "DDJ",
        "IZZETVSGOLGARI": "DDJ",
        "DDK": "DDK",
        "DUELDECKSORINVSTIBALT": "DDK",
        "DUELDECKSSORINVSTIBALT": "DDK",
        "SORINVSTIBALT": "DDK",
        "DDL": "DDL",
        "DUELDECKHEROESVSMONSTERS": "DDL",
        "DUELDECKSHEROESVSMONSTERS": "DDL",
        "HEROESVSMONSTERS": "DDL",
        "DDM": "DDM",
        "DUELDECKJACEVSVRASKA": "DDM",
        "DUELDECKSJACEVSVRASKA": "DDM",
        "JACEVSVRASKA": "DDM",
        "DGM": "DGM",
        "DRAGONSMAZE": "DGM",
        "DIS": "DIS",
        "DISSENSION": "DIS",
        "DARK": "DK",
        "DRK": "DK",
        "THEDARK": "DK",
        "DARKASCENSION": "DKA",
        "DKA": "DKA",
        "DECKMASTERSGARFIELDVSFINKEL": "DKM",
        "DKM": "DKM",
        "GARFIELDVSFINKEL": "DKM",
        "DCILEGENDMEMBERSHIP": "DLM",
        "DLM": "DLM",
        "DPA": "DPA",
        "DUELOFTHEPLANESWALKERS": "DPA",
        "DUELSOFTHEPLANESWALKERS": "DPA",
        "DUELSOFTHEPLANESWALKERSDECKS": "DPA",
        "DRAGONS": "DRB",
        "DRB": "DRB",
        "FROMTHEVAULTDRAGONS": "DRB",
        "FTVDRAGONS": "DRB",
        "DARKSTEEL": "DST",
        "DST": "DST",
        "EVE": "EVE",
        "EVENTIDE": "EVE",
        "DUELDECKELVESVSGOBLINS": "EVG",
        "DUELDECKSELVESVSGOBLINS": "EVG",
        "ELVESVSGOBLINS": "EVG",
        "EVG": "EVG",
        "EXO": "EX",
        "EXODUS": "EX",
        "FBP": "FBP",
        "FULLBOXPROMOTION": "FBP",
        "FALLENEMPIRES": "FE",
        "FEM": "FE",
        "FNM": "FNM",
        "FNMPROMO": "FNM",
        "FST": "FUT",
        "FUT": "FUT",
        "FUTURESIGHT": "FUT",
        "GAM": "GAM",
        "VIDEOGAMEPROMOS": "GAM",
        "GPT": "GPT",
        "GUILDPACT": "GPT",
        "GPX": "GPX",
        "GRANDPRIXPROMOS": "GPX",
        "GATECRASH": "GTC",
        "GTC": "GTC",
        "GATEWAY": "GTW",
        "GATEWAYANDWPNPROMOS": "GTW",
        "GATEWAYWPNPROMOS": "GTW",
        "GTW": "GTW",
        "LEGACY": "GU",
        "ULG": "GU",
        "URZASLEGACY": "GU",
        "H09": "H09",
        "PREMIUMDECKSERIESSLIVERS": "H09",
        "SLIVERS": "H09",
        "HAPPYHOLIDAYPROMOS": "HHL",
        "HHL": "HHL",
        "HML": "HM",
        "HOMELAND": "HM",
        "HOMELANDS": "HM",
        "HOP": "HOP",
        "PLANECHASE": "HOP",
        "I2P": "I2P",
        "INTRODUCTORYTWOPLAYER": "I2P",
        "ICE": "IA",
        "ICEAGE": "IA",
        "ICEAGES": "IA",
        "INV": "IN",
        "INVASION": "IN",
        "INNISTRAD": "ISD",
        "ISD": "ISD",
        "JGC": "JGC",
        "JUDGEGIFTCARDS": "JGC",
        "JOU": "JOU",
        "JOURNEYINTONYX": "JOU",
        "NYX": "JOU",
        "JUD": "JUD",
        "JUDGMENT": "JUD",
        "JUN": "JUN",
        "JUNIORSERIESPROMOS": "JUN",
        "LEG": "LE",
        "LEGEND": "LE",
        "LEGENDS": "LE",
        "LEGION": "LGN",
        "LEGIONS": "LGN",
        "LGN": "LGN",
        "ALTERNATEARTLANDS": "LND",
        "LND": "LND",
        "LORWYN": "LRW",
        "LRW": "LRW",
        "2010": "M10",
        "M10": "M10",
        "M2010": "M10",
        "MAGIC2010": "M10",
        "2011": "M11",
        "M11": "M11",
        "M2011": "M11",
        "MAGIC2011": "M11",
        "2012": "M12",
        "M12": "M12",
        "M2012": "M12",
        "MAGIC2012": "M12",
        "2013": "M13",
        "M13": "M13",
        "M2013": "M13",
        "MAGIC2013": "M13",
        "2014": "M14",
        "M14": "M14",
        "M2014": "M14",
        "MAGIC2014": "M14",
        "2015": "M15",
        "M15": "M15",
        "M2015": "M15",
        "MAGIC2015": "M15",
        "BESIEGED": "MBS",
        "MBS": "MBS",
        "MIRRODINBESIEGED": "MBS",
        "MAGICGAMEDAY": "MGD",
        "MGD": "MGD",
        "MIR": "MI",
        "MIRAGE": "MI",
        "MAGAZINEINSERTS": "MIN",
        "MIN": "MIN",
        "MASQUE": "MM",
        "MASQUES": "MM",
        "MERCADIANMASQUE": "MM",
        "MERCADIANMASQUES": "MM",
        "MMQ": "MM",
        "MMA": "MMA",
        "MODERNMASTERS": "MMA",
        "MOR": "MOR",
        "MORNINGTIDE": "MOR",
        "MAGICPLAYREWARDS": "MPR",
        "MPR": "MPR",
        "MIRRODIN": "MRD",
        "MRD": "MRD",
        "NEMESIS": "NE",
        "NMS": "NE",
        "NEWPHYREXIA": "NPH",
        "NPH": "NPH",
        "ODY": "OD",
        "ODYSSEY": "OD",
        "ONS": "ONS",
        "ONSLAUGHT": "ONS",
        "P02": "P2",
        "PORTAL2": "P2",
        "PORTALSECONDAGE": "P2",
        "PORTALTWO": "P2",
        "SECONDAGE": "P2",
        "1999": "P3",
        "S99": "P3",
        "STARTER1999": "P3",
        "STARTER99": "P3",
        "2000": "P4",
        "S00": "P4",
        "STARTER00": "P4",
        "STARTER2000": "P4",
        "PC2": "PC2",
        "PLANECHASE2012EDITION": "PC2",
        "PLANECHASE2012": "PC2",
        "FIRELIGHTNING": "PD2",
        "FIREANDLIGHTNING": "PD2",
        "PD2": "PD2",
        "PREMIUMDECKSERIESFIRELIGHTNING": "PD2",
        "PREMIUMDECKSERIESFIREANDLIGHTNING": "PD2",
        "GRAVEBORN": "PD3",
        "PD3": "PD3",
        "PREMIUMDECKSERIESGRAVEBORN": "PD3",
        "PORTAL3": "PK",
        "PORTALTHREE": "PK",
        "PORTALTHREEKINGDOMS": "PK",
        "PTK": "PK",
        "THREEKINGDOMS": "PK",
        "PLANARCHAOS": "PLC",
        "PLC": "PLC",
        "POR": "PO",
        "PORTAL": "PO",
        "PORTAL1": "PO",
        "PORTALONE": "PO",
        "PCY": "PR",
        "PROPHECY": "PR",
        "PRE": "PRE",
        "PRERELEASEPROMOS": "PRE",
        "PLANESHIFT": "PS",
        "PLS": "PS",
        "PROTOURPROMOS": "PTR",
        "PTR": "PTR",
        "RAV": "RAV",
        "RAVNICA": "RAV",
        "RAVNICACITYOFGUILDS": "RAV",
        "REL": "REL",
        "RELEASE&LAUNCHPARTIES": "REL",
        "ELDRAZI": "ROE",
        "RISE": "ROE",
        "RISEOFTHEELDRAZI": "ROE",
        "ROE": "ROE",
        "RETURNTORAVNICA": "RTR",
        "RTR": "RTR",
        "SCG": "SCG",
        "SCOURGE": "SCG",
        "SHADOWMOOR": "SHM",
        "SHADOWMORE": "SHM",
        "SHM": "SHM",
        "SAVIORS": "SOK",
        "SAVIORSOFKAMIGAWA": "SOK",
        "SOK": "SOK",
        "SCARS": "SOM",
        "SCARSOFMIRRODIN": "SOM",
        "SOM": "SOM",
        "STH": "ST",
        "STRONGHOLD": "ST",
        "STO": "STO",
        "STOREPROMOS": "STO",
        "SUM": "SUM",
        "SUMMEROFMAGICPROMOS": "SUM",
        "TEMPEST": "TE",
        "TMP": "TE",
        "THG": "THG",
        "TWOHEADEDGIANTPROMOS": "THG",
        "THEROS": "THS",
        "THS": "THS",
        "TOR": "TOR",
        "TORMENT": "TOR",
        "TIMESHIFTED": "TSB",
        "TSB": "TSB",
        "TIMESPIRAL": "TSP",
        "TSP": "TSP",
        "UGL": "UG",
        "UNGLUED": "UG",
        "UNH": "UNH",
        "UNHINGED": "UNH",
        "ULTRARARECARDS": "URC",
        "URC": "URC",
        "SAGA": "UZ",
        "URZASSAGA": "UZ",
        "USG": "UZ",
        "FROMTHEVAULTEXILED": "V09",
        "V09": "V09",
        "FTVEXILED": "V09",
        "EXILED": "V09",
        "FROMTHEVAULTRELICS": "V10",
        "V10": "V10",
        "FTVRELICS": "V10",
        "RELICS": "V10",
        "FROMTHEVAULTLEGENDS": "V11",
        "V11": "V11",
        "FTVLEGENDS": "V11",
        "FROMTHEVAULTREALMS": "V12",
        "FTVREALMS": "V12",
        "REALMS": "V12",
        "V12": "V12",
        "20": "V13",
        "FROMTHEVAULTTWENTY": "V13",
        "FTV20": "V13",
        "FTVTWENTY": "V13",
        "TWENTY": "V13",
        "V13": "V13",
        "ANNIHILATION": "V14",
        "FROMTHEVAULTANNIHILATION": "V14",
        "FTVANNIHILATION": "V14",
        "V14": "V14",
        "VIS": "VI",
        "VISIONS": "VI",
        "WEATHERLIGHT": "WL",
        "WTH": "WL",
        "CHAMPIONSHIPPRIZES": "WLD",
        "WLD": "WLD",
        "WORLDWAKE": "WWK",
        "WWK": "WWK",
        "ZEN": "ZEN",
        "ZENDIKAR": "ZEN"
    }

};
