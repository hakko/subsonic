function noop() {
  return false;
}

function popup(mylink, windowname) {
  return popupSize(mylink, windowname, 400, 200);
}

function popupSize(mylink, windowname, width, height) {
  var href;
  if (typeof (mylink) == "string") {
    href = mylink;
  } else {
    href = mylink.href;
  }

  var w = window.open(href, windowname, "width=" + width + ",height=" + height
      + ",scrollbars=yes,resizable=yes");
  w.focus();
  w.moveTo(300, 200);
  return false;
}

function loadFrame(el) {
  el = jQuery(el);
  el.load(el.data("src"));
  return false;
}
function findTarget(el) {
  el = jQuery(el);
  var target = el.data("target");
  if(target) {
    return target;
  }
  
  target = el.attr("target");
  if (!target) {
    target = "main";

    var parents = el.parents("[data-target]");
    if (parents.length > 0) {
      target = jQuery(parents[0]).data("target");
    }
  }
  return target;
}
function loadInFrame(el, href) {
  el = jQuery(el);
  target = findTarget(el);
  jQuery("." + target).load(href);

}

function submitForm(el, msg) {
  el = jQuery(el);

  var form = el;
  if (el.length == 1 && el[0].tagName.toLowerCase() != 'form') {
    form = el.parents('form');
  }

  var action = form.attr("action");
  jQuery.post(action, form.serialize(), function(data) {
    target = findTarget(el);
    jQuery("." + target).html(data);
    if(msg) {
      var statusMessage = jQuery("." + target).find(".statusMessage").html('<button type="button" class="close" data-dismiss="alert" aria-hidden="true">&times;</button>' + msg).addClass("alert alert-success alert-dismissable");
    }
  }).fail(function(jqXHR, textStatus, errorThrown) {
    var statusMessage = jQuery("." + target).find(".statusMessage").html('<button type="button" class="close" data-dismiss="alert" aria-hidden="true">&times;</button>' + errorThrown).addClass("alert alert-danger alert-dismissable");
  })
  return false;
}
function search(el, page) {
  try {
    el = jQuery(el);

    var form = el;
    if (el.length == 1 && el[0].tagName.toLowerCase() != 'form') {
      form = el.parents('form');
    }
    var data = form.serialize();
    if (!page) {
      page = 0;
    }
    data += "&page=" + page;
    jQuery('#songs').load('advancedSearchResult.view?' + data);
    window.scrollTo(0, 0);
  } catch(e) {
    window.console.log(e);
  }
  return false;
}
function dwrErrorHandler(msg, exc) {
  window.console.trace();
  window.console.log(msg);
  window.console.log(exc);
}

function changeClass(elem, className1, className2) {
  elem.className = (elem.className == className1) ? className2 : className1;
}
function playGenreRadio() {
  var genres = new Array();
  var e = document.getElementsByTagName("span");
  for ( var i = 0; i < e.length; i++) {
    if (e[i].className == "on") {
      genres.push(e[i].firstChild.data.trim());
    }
  }
  window.console.log(genres);
  onPlayGenreRadio(genres);
  return false;
}
var chooseDevice_modal_tmpl = '<div class="modal fade" id="chooseDeviceModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">' +
'  <div class="modal-dialog">' +
'    <div class="modal-content">' +
'      <div class="modal-header">' +
'        <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>' +
'        <h4 class="modal-title" id="myModalLabel">{{title}}</h4>' +
'      </div>' +
'      <div class="modal-body">' +
'        <select id="chooseDeviceSelect">' +
'          {{#devices}}' + 
'            <option value="{{serial}}">{{name}}</option>' +
'          {{/devices}}' +
'        </select>' +
'      </div>' +
'      <div class="modal-footer">' +
'        <button type="button" class="btn btn-primary" onclick="return saveDevice(this);">Ok</button>' +
'        <button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>' +
'      </div>' +
'    </div>' +
'  </div>' +
'</div>'
function chooseDevice(response) {
  var devices = [];
  jQuery.each(response, function(key, value) {
    devices[devices.length] = {"serial": key, "name": value};
  });
  if(jQuery("#chooseDeviceModal").length == 0) {
    jQuery(Mustache.render(chooseDevice_modal_tmpl, {"title": "Choose Device", "devices": devices})).modal();
  } else {
    jQuery("#chooseDeviceModal").modal("show");
  }
}
function saveDevice() {
  jQuery("#deviceName").val(jQuery("#chooseDeviceSelect").val());
  jQuery("#chooseDeviceModal").modal("hide");
  return true;
}


var app = (function() {
  $ = this.jQuery;
  var module = {};
  
  module.loadMain = function(path) {
    module.loadIndex(true);
    $.when($.ajax( path)).then(function( data, textStatus, jqXHR ) {
      jQuery('div.main').html(data);
    });
  }
  
  module.loadIndex = function(skipMain) {
    
    if($('div.upper').html().trim() != "") {
      return;
    }
    window.console.log("Loading everything");
    $.when( $.ajax( "top.view" ) ).then(function( data, textStatus, jqXHR ) {
      jQuery('div.upper').html(data);
    });
    $.when( $.ajax( "left.view" ) ).then(function( data, textStatus, jqXHR ) {
      jQuery('div.left').html(data);
    });
    $.when( $.ajax( "right.view" ) ).then(function( data, textStatus, jqXHR ) {
      jQuery('div.right').html(data);
    });
    if(!skipMain) {
      module.loadMain("nowPlaying.view");
    }
    $.when( $.ajax( "playlist.view" ) ).then(function( data, textStatus, jqXHR ) {
      jQuery('div.playlist').html(data);
    });
  };

  module.loadArtist = function(artistId, albumId) {
    module.loadIndex(true);
    var url ="artist.view?idUtf8Hex=" + artistId;
    if(albumId) {
      url += "&albumIdUtf8Hex=" + albumId;
    }
    // we should test if this is already loaded
    $.when( $.ajax( url ) ).then(function( data, textStatus, jqXHR ) {
      jQuery('div.main').html(data);
      // we should do the expansion here
      if(albumId) {
        var id = $('div[meta-album="' + albumId + '"]').attr("id").replace("alb", "");
        toggleAlbum(id);
      }
    });
  }
  module.loadHome = function(listType, listGroup) {
    var url = "home.view";
    if(listType) {
      url += "?listType=" + listType;
    }
    if(listGroup) {
      url += "&listGroup=" + listGroup;
    }
    module.loadMain(url);
  }
  module.loadGenres = function() {
    module.loadMain("genres.view");
  }
  module.loadRadio = function() {
    module.loadMain("radio.view");
  }
  module.loadFileTree = function() {
    module.loadMain("fileTree.view");
  }
  module.loadPodcastReceiver = function() {
    module.loadMain("podcastReceiver.view");
  }
  module.loadNowPlaying = function() {
    module.loadMain("nowPlaying.view");
  }
  module.loadSettings = function() {
    module.loadMain("settings.view");
  }
  module.loadStatus = function() {
    module.loadMain("status.view");
  }
  module.loadHelp = function() {
    module.loadMain("help.view");
  }
  module.loadPersonalSettings = function() {
    module.loadMain("personalSettings.view");
  }
  module.loadPlayerSettings = function() {
    module.loadMain("playerSettings.view");
  }
  module.loadPasswordSettings = function() {
    module.loadMain("passwordSettings.view");
  }
  module.loadShareSettings = function() {
    module.loadMain("shareSettings.view");
  }
  
  
  
  module.routes = {
      '/': module.loadIndex,
      '/home': module.loadHome,
      '/home/listType/:type': module.loadHome,
      '/home/listType/:type/listGroup/:group': module.loadHome,
      '/genres': module.loadGenres,
      '/radio': module.loadRadio,
      '/fileTree': module.loadFileTree,
      '/podcastReceiver': module.loadPodcastReceiver,
      '/nowPlaying': module.loadNowPlaying,
      '/settings': module.loadSettings,
      '/status': module.loadStatus,
      '/help': module.loadHelp,
      '/personalSettings': module.loadPersonalSettings,
      '/passwordSettings': module.loadPasswordSettings,
      '/playerSettings': module.loadPlayerSettings,
      '/shareSettings': module.loadShareSettings,
      '/artist/idUtf8Hex/:id': module.loadArtist,
      '/artist/idUtf8Hex/:id/albumIdUtf8Hex/:album_id': module.loadArtist
  };
  return module;
})();

    /*
    $.when( $.ajax( "playlist.view" ) ).then(function( data, textStatus, jqXHR ) {
      jQuery('div.playlist').html(data);
    });
    
    this.load('artist.view?idUtf8Hex=' + artistId).then(function(output) {
      // TODO: Scroll to this and highlight in left hand bar
      this.app.swap(output);
//      if(callback) {
//        callback.call();
//      }
    })
    */
  /*
  this.get('#/artist/idUtf8Hex/:id', function() {
    this.loadArtist(this.params['id']);
  });
  
  this.get('#/artist/idUtf8Hex/:id/albumIdUtf8Hex/:album_id', function(context) {
    this.loadArtist(this.params['id'], function() {
      context.log("I need to toggle the album")
    });
  });
  */
