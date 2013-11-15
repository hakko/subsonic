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
  var target = el.attr("target");
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
