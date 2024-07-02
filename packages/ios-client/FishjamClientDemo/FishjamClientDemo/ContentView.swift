//
//  ContentView.swift
//  FishjamClientDemo
//
//  Created by Karol Sygiet on 26/05/2023.
//

import SwiftUI

struct ContentView: View {
    @ObservedObject var contentViewController: ContentViewController
    @State private var token: String = ""

    init() {
        self.contentViewController = ContentViewController()
    }

    @ViewBuilder
    func participantsVideoViews(_ participantVideos: [ParticipantVideo], size: CGFloat) -> some View {
        ScrollView(.vertical) {
            VStack {
                ForEach(Array(stride(from: 0, to: participantVideos.count, by: 2)), id: \.self) { index in
                    HStack {
                        Spacer()
                        ParticipantVideoView(participantVideos[index], height: size, width: size)
                        Spacer()

                        if index + 1 < participantVideos.count {
                            ParticipantVideoView(participantVideos[index + 1], height: size, width: size)
                            Spacer()
                        }
                    }
                    Spacer()
                }
            }
        }
    }

    func connect() {
        contentViewController.connect(peerToken: token)
        contentViewController.connected = true
    }

    func disconnect() {
        contentViewController.disconnect()
        contentViewController.connected = false
    }

    var body: some View {
        VStack {
            Text(contentViewController.errorMessage ?? "")
                .padding()
                .foregroundColor(.red)
                .font(.system(size: 23, weight: .bold))
            if contentViewController.connected {
                Button("Disconnect", action: disconnect)
                participantsVideoViews(contentViewController.participantVideos, size: 100)
            } else {
                VStack(alignment: .leading) {
                    Text("Peer token")
                        .padding()
                        .foregroundColor(.blue)
                        .font(.system(size: 16, weight: .bold))

                    TextField("peer token", text: $token)
                        .padding()
                        .background(Color.white)
                        .cornerRadius(6)
                        .padding(4)
                        .overlay(
                            RoundedRectangle(cornerRadius: 6)
                                .stroke(Color.blue, lineWidth: 2.5)
                        )
                        .padding(3)
                        .foregroundColor(.blue)
                    Button("Connect", action: connect)
                }
            }
        }
        .padding()
    }
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}
