(function (_, $module$obsidian) {
  'use strict';
  //region block: imports
  var Notice = $module$obsidian.Notice;
  var MarkdownView = $module$obsidian.MarkdownView;
  var Plugin = $module$obsidian.Plugin;
  var Modal = $module$obsidian.Modal;
  var PluginSettingTab = $module$obsidian.PluginSettingTab;
  var Setting = $module$obsidian.Setting;
  //endregion
  //region block: pre-declaration
  class Unit {}
  class MyPlugin extends Plugin {
    constructor(app, manifest) {
      super(app, manifest);
      this.b_1 = new DefaultSettings();
    }
    c(_set____db54di) {
      this.b_1 = _set____db54di;
    }
    d() {
      return this.b_1;
    }
    e() {
      console.log('Loading MyPlugin in Kotlin!');
      return new Promise(MyPlugin$onload$lambda(this));
    }
    onload() {
      return this.e();
    }
    f() {
      console.log('Unloading MyPlugin');
    }
    onunload() {
      return this.f();
    }
    g() {
      var tmp = this.saveData(this.b_1);
      return tmp.then(MyPlugin$saveSettings$lambda);
    }
  }
  class default_0 extends MyPlugin {}
  class DefaultSettings {
    constructor() {
      this.l_1 = 'default';
    }
    m(_set____db54di) {
      this.l_1 = _set____db54di;
    }
    n() {
      return this.l_1;
    }
  }
  class CommandBuilder$build$cmd$1 {
    constructor(this$0) {
      this.o_1 = this$0.y_1;
      this.p_1 = this$0.z_1;
      this.q_1 = this$0.a1_1;
      this.r_1 = this$0.b1_1;
      this.s_1 = this$0.c1_1;
      this.t_1 = this$0.d1_1;
      this.u_1 = this$0.e1_1;
      this.v_1 = this$0.f1_1;
      this.w_1 = this$0.g1_1;
      this.x_1 = this$0.h1_1;
      delete this.id;
      delete this.name;
      delete this.icon;
      delete this.mobileOnly;
      delete this.repeatable;
      delete this.callback;
      delete this.checkCallback;
      delete this.editorCallback;
      delete this.editorCheckCallback;
      delete this.hotkeys;
    }
    i1(_set____db54di) {
      this.o_1 = _set____db54di;
    }
    j1() {
      return this.o_1;
    }
    k1(_set____db54di) {
      this.p_1 = _set____db54di;
    }
    l1() {
      return this.p_1;
    }
    m1(_set____db54di) {
      this.q_1 = _set____db54di;
    }
    n1() {
      return this.q_1;
    }
    o1(_set____db54di) {
      this.r_1 = _set____db54di;
    }
    p1() {
      return this.r_1;
    }
    q1(_set____db54di) {
      this.s_1 = _set____db54di;
    }
    r1() {
      return this.s_1;
    }
    s1(_set____db54di) {
      this.t_1 = _set____db54di;
    }
    t1() {
      return this.t_1;
    }
    u1(_set____db54di) {
      this.u_1 = _set____db54di;
    }
    v1() {
      return this.u_1;
    }
    w1(_set____db54di) {
      this.v_1 = _set____db54di;
    }
    x1() {
      return this.v_1;
    }
    y1(_set____db54di) {
      this.w_1 = _set____db54di;
    }
    z1() {
      return this.w_1;
    }
    a2(_set____db54di) {
      this.x_1 = _set____db54di;
    }
    b2() {
      return this.x_1;
    }
    get id() {
      return this.j1();
    }
    set id(value) {
      this.i1(value);
    }
    get name() {
      return this.l1();
    }
    set name(value) {
      this.k1(value);
    }
    get icon() {
      return this.n1();
    }
    set icon(value) {
      this.m1(value);
    }
    get mobileOnly() {
      return this.p1();
    }
    set mobileOnly(value) {
      this.o1(value);
    }
    get repeatable() {
      return this.r1();
    }
    set repeatable(value) {
      this.q1(value);
    }
    get callback() {
      return this.t1();
    }
    set callback(value) {
      this.s1(value);
    }
    get checkCallback() {
      return this.v1();
    }
    set checkCallback(value) {
      this.u1(value);
    }
    get editorCallback() {
      return this.x1();
    }
    set editorCallback(value) {
      this.w1(value);
    }
    get editorCheckCallback() {
      return this.z1();
    }
    set editorCheckCallback(value) {
      this.y1(value);
    }
    get hotkeys() {
      return this.b2();
    }
    set hotkeys(value) {
      this.a2(value);
    }
  }
  class CommandBuilder {
    constructor() {
      this.y_1 = '';
      this.z_1 = '';
      this.a1_1 = null;
      this.b1_1 = null;
      this.c1_1 = null;
      this.d1_1 = null;
      this.e1_1 = null;
      this.f1_1 = null;
      this.g1_1 = null;
      this.h1_1 = null;
    }
    c2() {
      var cmd = new CommandBuilder$build$cmd$1(this);
      // Inline function 'kotlin.js.unsafeCast' call
      // Inline function 'kotlin.js.asDynamic' call
      return cmd;
    }
  }
  class MyPlugin$mergeSettings$1 {
    constructor($loaded, $defaults) {
      var tmp = this;
      // Inline function 'kotlin.takeIf' call
      var this_0 = $loaded.n();
      var tmp_0;
      // Inline function 'kotlin.text.isNotEmpty' call
      if (charSequenceLength(this_0) > 0) {
        tmp_0 = this_0;
      } else {
        tmp_0 = null;
      }
      var tmp0_elvis_lhs = tmp_0;
      tmp.d2_1 = tmp0_elvis_lhs == null ? $defaults.n() : tmp0_elvis_lhs;
    }
    m(_set____db54di) {
      this.d2_1 = _set____db54di;
    }
    n() {
      return this.d2_1;
    }
  }
  class SampleModal extends Modal {
    constructor(app) {
      super(app);
    }
    e2() {
      setText(this.contentEl, 'Woah!');
    }
    onOpen() {
      return this.e2();
    }
    f2() {
      empty(this.contentEl);
    }
    onClose() {
      return this.f2();
    }
  }
  class SampleSettingTab extends PluginSettingTab {
    constructor(app, myPlugin) {
      super(app, myPlugin);
      this.i2_1 = myPlugin;
    }
    j2() {
      empty(this.containerEl);
      var tmp = (new Setting(this.containerEl)).setName('Setting #1').setDesc("It's a secret");
      tmp.addText(SampleSettingTab$display$lambda(this));
    }
    display() {
      return this.j2();
    }
  }
  //endregion
  function implement(interfaces) {
    var maxSize = 1;
    var masks = [];
    var inductionVariable = 0;
    var last = interfaces.length;
    while (inductionVariable < last) {
      var i = interfaces[inductionVariable];
      inductionVariable = inductionVariable + 1 | 0;
      var currentSize = maxSize;
      var tmp0_elvis_lhs = i.prototype.$imask$;
      var imask = tmp0_elvis_lhs == null ? i.$imask$ : tmp0_elvis_lhs;
      if (!(imask == null)) {
        masks.push(imask);
        currentSize = imask.length;
      }
      var iid = i.$metadata$.iid;
      var tmp;
      if (iid == null) {
        tmp = null;
      } else {
        // Inline function 'kotlin.let' call
        tmp = bitMaskWith(iid);
      }
      var iidImask = tmp;
      if (!(iidImask == null)) {
        masks.push(iidImask);
        currentSize = Math.max(currentSize, iidImask.length);
      }
      if (currentSize > maxSize) {
        maxSize = currentSize;
      }
    }
    return compositeBitMask(maxSize, masks);
  }
  function bitMaskWith(activeBit) {
    var numberIndex = activeBit >> 5;
    var intArray = new Int32Array(numberIndex + 1 | 0);
    var positionInNumber = activeBit & 31;
    var numberWithSettledBit = 1 << positionInNumber;
    intArray[numberIndex] = intArray[numberIndex] | numberWithSettledBit;
    return intArray;
  }
  function compositeBitMask(capacity, masks) {
    var tmp = 0;
    var tmp_0 = new Int32Array(capacity);
    while (tmp < capacity) {
      var tmp_1 = tmp;
      var result = 0;
      var inductionVariable = 0;
      var last = masks.length;
      while (inductionVariable < last) {
        var mask = masks[inductionVariable];
        inductionVariable = inductionVariable + 1 | 0;
        if (tmp_1 < mask.length) {
          result = result | mask[tmp_1];
        }
      }
      tmp_0[tmp_1] = result;
      tmp = tmp + 1 | 0;
    }
    return tmp_0;
  }
  function isString(a) {
    return typeof a === 'string';
  }
  function charSequenceLength(a) {
    var tmp;
    if (isString(a)) {
      // Inline function 'kotlin.js.asDynamic' call
      // Inline function 'kotlin.js.unsafeCast' call
      tmp = a.length;
    } else {
      tmp = a.a();
    }
    return tmp;
  }
  function defineProp(obj, name, getter, setter, enumerable) {
    return Object.defineProperty(obj, name, {configurable: true, get: getter, set: setter, enumerable: enumerable});
  }
  function equals(obj1, obj2) {
    if (obj1 == null) {
      return obj2 == null;
    }
    if (obj2 == null) {
      return false;
    }
    if (typeof obj1 === 'object' && typeof obj1.equals === 'function') {
      return obj1.equals(obj2);
    }
    if (obj1 !== obj1) {
      return obj2 !== obj2;
    }
    if (typeof obj1 === 'number' && typeof obj2 === 'number') {
      var tmp;
      if (obj1 === obj2) {
        var tmp_0;
        if (obj1 !== 0) {
          tmp_0 = true;
        } else {
          // Inline function 'kotlin.js.asDynamic' call
          var tmp_1 = 1 / obj1;
          // Inline function 'kotlin.js.asDynamic' call
          tmp_0 = tmp_1 === 1 / obj2;
        }
        tmp = tmp_0;
      } else {
        tmp = false;
      }
      return tmp;
    }
    return obj1 === obj2;
  }
  function protoOf(constructor) {
    return constructor.prototype;
  }
  function createThis(ctor, box) {
    var self_0 = Object.create(ctor.prototype);
    boxApply(self_0, box);
    return self_0;
  }
  function boxApply(self_0, box) {
    if (box !== VOID) {
      Object.assign(self_0, box);
    }
  }
  function createExternalThis(ctor, superExternalCtor, parameters, box) {
    var tmp;
    if (box === VOID) {
      tmp = ctor;
    } else {
      var newCtor = class  extends ctor {}
      Object.assign(newCtor.prototype, box);
      newCtor.constructor = ctor;
      tmp = newCtor;
    }
    var selfCtor = tmp;
    return Reflect.construct(superExternalCtor, parameters, selfCtor);
  }
  function createMetadata(kind, name, defaultConstructor, associatedObjectKey, associatedObjects, suspendArity) {
    var undef = VOID;
    var iid = kind === 'interface' ? generateInterfaceId() : VOID;
    return {kind: kind, simpleName: name, associatedObjectKey: associatedObjectKey, associatedObjects: associatedObjects, suspendArity: suspendArity, $kClass$: undef, defaultConstructor: defaultConstructor, iid: iid};
  }
  function generateInterfaceId() {
    if (globalInterfaceId === VOID) {
      globalInterfaceId = 0;
    }
    // Inline function 'kotlin.js.unsafeCast' call
    globalInterfaceId = globalInterfaceId + 1 | 0;
    // Inline function 'kotlin.js.unsafeCast' call
    return globalInterfaceId;
  }
  var globalInterfaceId;
  function initMetadataForClass(ctor, name, defaultConstructor, parent, interfaces, suspendArity, associatedObjectKey, associatedObjects) {
    var kind = 'class';
    initMetadataFor(kind, ctor, name, defaultConstructor, parent, interfaces, suspendArity, associatedObjectKey, associatedObjects);
  }
  function initMetadataFor(kind, ctor, name, defaultConstructor, parent, interfaces, suspendArity, associatedObjectKey, associatedObjects) {
    if (!(parent == null)) {
      ctor.prototype = Object.create(parent.prototype);
      ctor.prototype.constructor = ctor;
    }
    var metadata = createMetadata(kind, name, defaultConstructor, associatedObjectKey, associatedObjects, suspendArity);
    ctor.$metadata$ = metadata;
    if (!(interfaces == null)) {
      var receiver = !equals(metadata.iid, VOID) ? ctor : ctor.prototype;
      receiver.$imask$ = implement(interfaces);
    }
  }
  function initMetadataForObject(ctor, name, defaultConstructor, parent, interfaces, suspendArity, associatedObjectKey, associatedObjects) {
    var kind = 'object';
    initMetadataFor(kind, ctor, name, defaultConstructor, parent, interfaces, suspendArity, associatedObjectKey, associatedObjects);
  }
  function initMetadataForLambda(ctor, parent, interfaces, suspendArity) {
    initMetadataForClass(ctor, 'Lambda', VOID, parent, interfaces, suspendArity, VOID, VOID);
  }
  function initMetadataForCoroutine(ctor, parent, interfaces, suspendArity) {
    initMetadataForClass(ctor, 'Coroutine', VOID, parent, interfaces, suspendArity, VOID, VOID);
  }
  function initMetadataForFunctionReference(ctor, parent, interfaces, suspendArity) {
    initMetadataForClass(ctor, 'FunctionReference', VOID, parent, interfaces, suspendArity, VOID, VOID);
  }
  function initMetadataForCompanion(ctor, parent, interfaces, suspendArity) {
    initMetadataForObject(ctor, 'Companion', VOID, parent, interfaces, suspendArity, VOID, VOID);
  }
  function get_VOID() {
    _init_properties_void_kt__3zg9as();
    return VOID;
  }
  var VOID;
  var properties_initialized_void_kt_e4ret2;
  function _init_properties_void_kt__3zg9as() {
    if (!properties_initialized_void_kt_e4ret2) {
      properties_initialized_void_kt_e4ret2 = true;
      VOID = void 0;
    }
  }
  var Unit_instance;
  function Unit_getInstance() {
    return Unit_instance;
  }
  function setupPlugin($this) {
    var ribbonIconEl = $this.addRibbonIcon('dice', 'Sample Plugin', MyPlugin$setupPlugin$lambda);
    addClass(ribbonIconEl, 'my-plugin-ribbon-class');
    var statusBarItemEl = $this.addStatusBarItem();
    setText(statusBarItemEl, 'Status Bar Text');
    var sampleModal = new SampleModal($this.app);
    // Inline function 'land.tbp.toolbox.command' call
    // Inline function 'kotlin.apply' call
    var this_0 = new CommandBuilder();
    this_0.y_1 = 'open-sample-modal-simple';
    this_0.z_1 = 'Open sample modal (simple)';
    var tmp = this_0;
    tmp.d1_1 = MyPlugin$setupPlugin$lambda_0(sampleModal);
    var tmp$ret$2 = this_0.c2();
    $this.addCommand(tmp$ret$2);
    // Inline function 'land.tbp.toolbox.command' call
    // Inline function 'kotlin.apply' call
    var this_1 = new CommandBuilder();
    this_1.y_1 = 'sample-editor-command';
    this_1.z_1 = 'Sample editor command';
    var tmp_0 = this_1;
    tmp_0.f1_1 = MyPlugin$setupPlugin$lambda_1;
    var tmp$ret$5 = this_1.c2();
    $this.addCommand(tmp$ret$5);
    // Inline function 'land.tbp.toolbox.command' call
    // Inline function 'kotlin.apply' call
    var this_2 = new CommandBuilder();
    this_2.y_1 = 'open-sample-modal-complex';
    this_2.z_1 = 'Open sample modal (complex)';
    var tmp_1 = this_2;
    tmp_1.e1_1 = MyPlugin$setupPlugin$lambda_2($this, sampleModal);
    var tmp$ret$8 = this_2.c2();
    $this.addCommand(tmp$ret$8);
    $this.addSettingTab(new SampleSettingTab($this.app, $this));
    var tmp_2 = document;
    $this.registerDomEvent(tmp_2, 'click', MyPlugin$setupPlugin$lambda_3);
    var tmp_3 = window;
    var intervalId = tmp_3.setInterval(MyPlugin$setupPlugin$lambda_4, 300000);
    $this.registerInterval(intervalId);
  }
  function loadSettings($this) {
    var tmp = $this.loadData();
    return tmp.then(MyPlugin$loadSettings$lambda($this));
  }
  function mergeSettings($this, defaults, loaded) {
    return new MyPlugin$mergeSettings$1(loaded, defaults);
  }
  function MyPlugin$onload$lambda$lambda(this$0, $resolve) {
    return (it) => {
      setupPlugin(this$0);
      $resolve(Unit_instance);
      return Unit_instance;
    };
  }
  function MyPlugin$onload$lambda$lambda_0($reject) {
    return (error) => {
      console.error('Error loading settings:', error);
      $reject(error);
      return Unit_instance;
    };
  }
  function MyPlugin$onload$lambda(this$0) {
    return (resolve, reject) => {
      try {
        var tmp = loadSettings(this$0);
        var tmp_0 = tmp.then(MyPlugin$onload$lambda$lambda(this$0, resolve));
        tmp_0.catch(MyPlugin$onload$lambda$lambda_0(reject));
      } catch ($p) {
        if ($p instanceof Error) {
          var e = $p;
          console.error('Error in onload:', e);
          reject(e);
        } else {
          throw $p;
        }
      }
      return Unit_instance;
    };
  }
  function MyPlugin$setupPlugin$lambda(_evt) {
    return new Notice('This is a notice!');
  }
  function MyPlugin$setupPlugin$lambda_0($sampleModal) {
    return () => {
      $sampleModal.open();
      return Unit_instance;
    };
  }
  function MyPlugin$setupPlugin$lambda_1(editor, _view) {
    console.log(editor.getSelection());
    editor.replaceSelection('Sample Editor Command');
    return Unit_instance;
  }
  function MyPlugin$setupPlugin$lambda_2(this$0, $sampleModal) {
    return (checking) => {
      var tmp = this$0.app.workspace;
      // Inline function 'kotlin.js.unsafeCast' call
      // Inline function 'kotlin.js.asDynamic' call
      var tmp$ret$1 = MarkdownView;
      var markdownView = tmp.getActiveViewOfType(tmp$ret$1);
      var tmp_0;
      if (!(markdownView == null)) {
        if (!checking) {
          $sampleModal.open();
        }
        tmp_0 = true;
      } else {
        tmp_0 = false;
      }
      return tmp_0;
    };
  }
  function MyPlugin$setupPlugin$lambda_3(evt) {
    console.log('click', evt);
    return Unit_instance;
  }
  function MyPlugin$setupPlugin$lambda_4() {
    console.log('setInterval');
    return Unit_instance;
  }
  function MyPlugin$loadSettings$lambda(this$0) {
    return (data) => {
      var tmp;
      if (!(data == null)) {
        var defaultSettings = new DefaultSettings();
        var tmp_0 = this$0;
        // Inline function 'kotlin.js.unsafeCast' call
        // Inline function 'kotlin.js.asDynamic' call
        tmp_0.b_1 = mergeSettings(this$0, defaultSettings, data);
        tmp = Unit_instance;
      }
      return Unit_instance;
    };
  }
  function MyPlugin$saveSettings$lambda(it) {
    return Unit_instance;
  }
  function SampleSettingTab$display$lambda$lambda(this$0) {
    return (value) => {
      this$0.i2_1.b_1.m(value);
      return this$0.i2_1.g();
    };
  }
  function SampleSettingTab$display$lambda(this$0) {
    return (text) => {
      var tmp = text.setPlaceholder('Enter your secret').setValue(this$0.i2_1.b_1.n());
      return tmp.onChange(SampleSettingTab$display$lambda$lambda(this$0));
    };
  }
  function addClass(_this__u8e3s4, className) {
    _this__u8e3s4.classList.add(className);
  }
  function setText(_this__u8e3s4, text) {
    _this__u8e3s4.textContent = text;
  }
  function empty(_this__u8e3s4) {
    _this__u8e3s4.innerHTML = '';
  }
  //region block: post-declaration
  initMetadataForObject(Unit, 'Unit');
  initMetadataForClass(MyPlugin, 'MyPlugin');
  initMetadataForClass(default_0, 'ObsidianPlugin');
  initMetadataForClass(DefaultSettings, 'DefaultSettings', DefaultSettings);
  initMetadataForClass(CommandBuilder$build$cmd$1);
  initMetadataForClass(CommandBuilder, 'CommandBuilder', CommandBuilder);
  initMetadataForClass(MyPlugin$mergeSettings$1);
  initMetadataForClass(SampleModal, 'SampleModal');
  initMetadataForClass(SampleSettingTab, 'SampleSettingTab');
  //endregion
  //region block: init
  Unit_instance = new Unit();
  //endregion
  //region block: exports
  function $jsExportAll$(_) {
    _.default = default_0;
  }
  $jsExportAll$(_);
  //endregion
  return _;
}(module.exports, require('obsidian')));

//# sourceMappingURL=shared.js.map
