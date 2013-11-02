function noop() {
  return false;
}

function popup(mylink, windowname) {
    return popupSize(mylink, windowname, 400, 200);
}

function popupSize(mylink, windowname, width, height) {
    var href;
    if (typeof(mylink) == "string") {
        href = mylink;
    } else {
        href = mylink.href;
    }

    var w = window.open(href, windowname, "width=" + width + ",height=" + height + ",scrollbars=yes,resizable=yes");
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
  if(!target) {
    target = "main";

    var parents = el.parents("[data-target]");
    if(parents.length > 0) {
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

function submitForm(el) {
  var forms = jQuery(el).parents("form");
  if(forms.length == 0) {
    return false;
  }
  var form = jQuery(forms[0]);
  var action = form.attr("action");
  jQuery.post(action, form.serialize(), function(data) {
    el = jQuery(el);
    target = findTarget(el);
    jQuery("." + target).html(data);
  }); 
  return false;
}
function search(el, page) {
  el = jQuery(el);
  
  var form = el;
  if(el.length == 1 && el[0].tagName.toLowerCase() != 'form') {
    form = el.parents('form');
  }
  var data = form.serialize();
  if(!page) {
    page = 0;
  }
  data += "&page=" + page;
  jQuery('#songs').load('advancedSearchResult.view?' + data);
  window.scrollTo(0, 0);
  return false;
}

