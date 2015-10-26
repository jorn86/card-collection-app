'use strict';

describe('the mana cost directive', function() {
    beforeEach(module('card-app'));

    var $compile, $rootScope;
    beforeEach(inject(function(_$compile_, _$rootScope_){
        $compile = _$compile_;
        $rootScope = _$rootScope_;
    }));

    var parse = function(mana) {
        var element = $compile('<manacost mana="\'' + mana + '\'"></manacost>')($rootScope);
        $rootScope.$digest();
        return element;
    };

    it('handles empty input', function() {
        var element = $compile('<manacost></manacost>')($rootScope);
        $rootScope.$digest();
        expect(element.html()).toBe('');
        expect(parse('').html()).toBe('');
    });

    it('parses a simple cost', function() {
        var element = parse('X1G');
        expect(element.children("img").size()).toBe(3);
    });

    it('parses a hybrid cost', function() {
        var element = parse('{UR}{WB}');
        expect(element.children("img").size()).toBe(2);
    });

    it('parses a number > 9 as single element', function() {
        var element = parse('16');
        expect(element.children("img").size()).toBe(1);
    });

    it('parses a long cost', function() {
        var element = parse('12WWUUBBRRGG');
        expect(element.children("img").size()).toBe(11);
    });

    it('parses a split cost', function() {
        var element = parse('R/{WU}');
        expect(element.children("img").size()).toBe(3);
    });
});
