import React, { Component } from 'react'
import {
    View, Dimensions,
    StyleSheet, ActivityIndicator,
    requireNativeComponent,
    UIManager, findNodeHandle,
    NativeModules, NativeEventEmitter,
    Platform,
} from "react-native"
import PropTypes from 'prop-types'

class IJKPlayerView extends Component {
    static propTypes = {
        showIndicator: PropTypes.bool,
        options: PropTypes.object,
        onComplete: PropTypes.func,
        onPrepared: PropTypes.func,
        onError: PropTypes.func,
        onInfo: PropTypes.func,
        onProgressUpdate: PropTypes.func,
        onLoadProgressUpdate: PropTypes.func,
    }
    constructor(props) {
        super(props)
        this.state = {
            indicatorLeft: null,
            indicatorTop: null,
            showIndicator: false,
        }
    }

    play = () => {
        console.log('play')
        UIManager.dispatchViewManagerCommand(
            findNodeHandle(this.ref),
            UIManager.getViewManagerConfig('RNEasyIjkplayerView').Commands.play,
            null,
        )
    }

    pause = () => {
        console.log('pause')
        UIManager.dispatchViewManagerCommand(
            findNodeHandle(this.ref),
            UIManager.getViewManagerConfig('RNEasyIjkplayerView').Commands.pause,
            null,
        )
    }

    stop = () => {
        console.log('pause')
        UIManager.dispatchViewManagerCommand(
            findNodeHandle(this.ref),
            UIManager.getViewManagerConfig('RNEasyIjkplayerView').Commands.stop,
            null,
        )
    }

    seekTo = (time) => {
        UIManager.dispatchViewManagerCommand(
            findNodeHandle(this.ref),
            UIManager.getViewManagerConfig('RNEasyIjkplayerView').Commands.seekTo,
            [time],
        )
    }

    releasePlayer = () => {
        if (Platform.OS === 'ios') {
            UIManager.dispatchViewManagerCommand(
                findNodeHandle(this.ref),
                UIManager.getViewManagerConfig('RNEasyIjkplayerView').Commands.releasePlayer,
                null,
            )
        }
    }

    componentWillUnmount() {
        this.releasePlayer();
    }

    /**
     *
     * @param callback
     */
    getDuration = (callback) => {
        NativeModules.RNEasyIjkplayerView.getDuration(findNodeHandle(this.ref), callback)
    }

    getSize = (callback) => {
        NativeModules.RNEasyIjkplayerView.getSize(findNodeHandle(this.ref), callback)
    }

    _onProgressUpdate = ({ nativeEvent: { progress } }) => {
        const { onProgressUpdate } = this.props
        onProgressUpdate && onProgressUpdate(progress)
    }

    _onPrepared = ({nativeEvent}) => {
        console.log('on prepared')
        const { onPrepared } = this.props
        onPrepared && onPrepared(nativeEvent)
    }

    _onLoadProgressUpdate = ({ nativeEvent }) => {
        console.log('on loadProgressUpdate:', nativeEvent)
        const { onLoadProgressUpdate } = this.props
        onLoadProgressUpdate && onLoadProgressUpdate(nativeEvent)
    }

    _onInfo = ({ nativeEvent }) => {
        console.log('on Info:', nativeEvent)
        const { onInfo } = this.props
        onInfo && onInfo(nativeEvent)
    }

    _onError = ({ nativeEvent: { error } }) => {
        console.log('on error:', error)
        const { onError } = this.props
        onError && onError(error)
    }

    _onComplete = () => {
        const { onComplete } = this.props
        onComplete && onComplete()
    }

    render() {
        return (
            <IJKPlayer
                {...this.props}
                ref={ref => this.ref = ref}
                onPrepared={this._onPrepared}
                onProgressUpdate={this._onProgressUpdate}
                onLoadProgressUpdate={this._onLoadProgressUpdate}
                onInfo={this._onInfo}
                onError={this._onError}
                onComplete={this._onComplete}
            />
        )
    }
}

var IJKPlayer = requireNativeComponent('RNEasyIjkplayerView', IJKPlayerView)

export default IJKPlayerView
